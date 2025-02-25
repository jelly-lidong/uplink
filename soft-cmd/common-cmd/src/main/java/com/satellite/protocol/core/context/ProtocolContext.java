package com.satellite.protocol.core.context;

import com.satellite.protocol.core.composite.ProtocolComponent;
import com.satellite.protocol.core.function.FunctionContext;
import com.satellite.protocol.core.function.FunctionRegistry;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.LengthUnit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * 协议上下文 - 处理节点间的依赖关系
 * 存储节点值并提供节点间的值引用能力
 */
@Slf4j
public class ProtocolContext {

    /**
     * 存储节点值的Map
     */
    private final Map<String, Object> nodeValues = new ConcurrentHashMap<>();

    /**
     * 存储待处理的依赖节点
     */
    private final Map<String, DependentNode> pendingNodes = new HashMap<>();

    /**
     * 当前处理的组件树根节点
     */
    private final ProtocolComponent rootComponent;

    /**
     * 存储已编码的数据
     */
    private ByteBuf encodedData;

    /**
     * 记录每个组件的数据范围
     */
    private final Map<String, ByteRange> componentRanges = new HashMap<>();

    /**
     * 当前处理的节点路径
     */
    private String currentPath;

    /**
     * 函数注册表
     */

    public ProtocolContext(ProtocolComponent rootComponent, ByteBuf buffer) {
        this.rootComponent = rootComponent;
        this.encodedData = buffer;
        this.currentPath = "/protocol"; // 初始化为根路径
    }

    /**
     * 存储节点值
     *
     * @param path  节点完整路径
     * @param value 值
     */
    public void putNodeValue(String path, Object value) {
        log.debug("存储节点值, 路径: {}, 值: {}", path, value);
        nodeValues.put(path, value);
        processPendingNodes(path);
    }

    /**
     * 获取节点值
     *
     * @param path 节点完整路径
     * @return 节点值
     */
    public Object getNodeValue(String path) {
        Object value = nodeValues.get(path);
        log.debug("获取节点值, 路径: {}, 值: {}", path, value);
        return value;
    }

    /**
     * 添加待处理的依赖节点
     *
     * @param node           依赖节点
     * @param dependencyKeys 依赖的节点key列表
     */
    public void addPendingNode(Node node, String... dependencyKeys) {
        String nodeKey = generateNodeKey(node);
        log.debug("添加待处理节点: {}, 依赖节点: {}", nodeKey, String.join(",", dependencyKeys));
        pendingNodes.put(nodeKey, new DependentNode(node, dependencyKeys));
    }

    /**
     * 处理待处理节点
     *
     * @param updatedKey 更新的节点key
     */
    private void processPendingNodes(String updatedKey) {
        pendingNodes.entrySet().removeIf(entry -> {
            DependentNode dependentNode = entry.getValue();
            if (dependentNode.isDependencySatisfied(this)) {
                log.debug("处理待处理节点: {}", entry.getKey());
                try {
                    // 计算并更新节点值
                    processNode(dependentNode);
                    return true;
                } catch (Exception e) {
                    log.error("处理待处理节点失败: {}, 原因: {}", entry.getKey(), e.getMessage());
                    return false;
                }
            }
            return false;
        });
    }

    /**
     * 处理依赖节点
     *
     * @param dependentNode 待处理的依赖节点
     */
    private void processNode(DependentNode dependentNode) {
        Node node = dependentNode.getNode();
        String refValue = node.getRefValue();

        // 计算表达式的值
        Object value = evaluateExpression(refValue, dependentNode.getDependencyKeys());

        // 更新节点值
        node.setValue(value.toString());

        // 将节点值存入上下文
        String nodePath = generateNodeKey(node);
        putNodeValue(nodePath, value);

        log.debug("更新节点值: {}, 值: {}", nodePath, value);
    }

    /**
     * 计算表达式
     */
    public Object evaluateExpression(String expression, String[] dependencyKeys) {
        log.debug("计算表达式: {}, 依赖节点: {}", expression, String.join(",", dependencyKeys));

        try {
            // 直接将表达式传递给 FunctionRegistry 处理
            FunctionContext context = new FunctionContext(this);
            // 替换表达式中的节点引用
            String evaluatedExpression = replaceNodeReferences(expression, context);
            Object result = FunctionRegistry.evaluate(evaluatedExpression, context);
            log.debug("计算表达式结果: {} = {}", expression, result);
            return result;
        } catch (Exception e) {
            log.error("表达式计算失败: {}", expression, e);
            throw new IllegalArgumentException(
                    String.format("表达式计算失败: %s (%s)", expression, e.getMessage()));
        }
    }

    private static String replaceNodeReferences(String expression, FunctionContext context) {
        // 替换所有 ${...} 形式的节点引用
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(expression);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String nodePath = matcher.group(1);
            Object value = context.getProtocolContext().getNodeValue(nodePath);
            if (value == null) {
                throw new IllegalArgumentException("未找到节点值: " + nodePath);
            }
            matcher.appendReplacement(result, value.toString());
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 获取组件数据的上下文方法
     * 供函数执行时使用
     */
    public byte[] getComponentBytes(String path) {
        String resolvedPath = resolveNodePath(currentPath, path);
        return getComponentData(resolvedPath);
    }

    /**
     * 获取组件长度的上下文方法
     * 供函数执行时使用
     */
    public int getComponentLength(String path) {
        String resolvedPath = resolveNodePath(currentPath, path);
        ByteRange range = componentRanges.get(resolvedPath);
        if (range == null) {
            throw new IllegalArgumentException("未找到组件数据: " + resolvedPath);
        }
        return range.getLengthInBytes();
    }

    /**
     * 生成节点的唯一key
     *
     * @param node 节点
     * @return 节点key
     */
    private String generateNodeKey(Node node) {
        return node.getName(); // 可以根据需要使用更复杂的key生成策略
    }

    /**
     * 添加已编码的数据
     */
    public void addEncodedData(byte[] data) {
        encodedData.writeBytes(data);
    }

    /**
     * 获取已编码的数据
     */
    public byte[] getEncodedData() {
        byte[] data = new byte[encodedData.readableBytes()];
        encodedData.getBytes(0, data);
        return data;
    }

    /**
     * 清理上下文
     */
    public void clear() {
        nodeValues.clear();
        pendingNodes.clear();
        encodedData.release();
        encodedData = Unpooled.buffer();
    }

    /**
     * 依赖节点类 - 存储节点及其依赖信息
     */
    private static class DependentNode {

        private final Node node;
        private final String[] dependencyKeys;

        public DependentNode(Node node, String[] dependencyKeys) {
            this.node = node;
            this.dependencyKeys = dependencyKeys;
        }

        public Node getNode() {
            return node;
        }

        public String[] getDependencyKeys() {
            return dependencyKeys;
        }

        /**
         * 检查依赖是否都满足
         */
        public boolean isDependencySatisfied(ProtocolContext context) {
            for (String key : dependencyKeys) {
                if (!context.nodeValues.containsKey(key)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 根据相对路径或绝对路径获取节点的完整路径
     *
     * @param currentPath 当前节点路径
     * @param targetPath  目标路径(可以是相对路径或绝对路径)
     * @return 完整路径
     */
    public String resolveNodePath(String currentPath, String targetPath) {
        log.debug("解析节点路径, 当前路径: {}, 目标路径: {}", currentPath, targetPath);

        // 处理绝对路径
        if (targetPath.startsWith("/")) {
            return normalizePath(targetPath);
        }

        // 处理相对路径
        String[] currentSegments = currentPath.split("/");
        String[] targetSegments = targetPath.split("/");

        int currentIndex = currentSegments.length - 1;
        int targetIndex = 0;

        // 处理上级路径引用 "../"
        while (targetIndex < targetSegments.length && targetSegments[targetIndex].equals("..")) {
            currentIndex--;
            targetIndex++;
            if (currentIndex < 0) {
                throw new IllegalArgumentException(
                        String.format("无效的相对路径: %s (从 %s)", targetPath, currentPath));
            }
        }

        // 构建最终路径
        StringBuilder resultPath = new StringBuilder();

        // 添加当前路径的前缀部分
        for (int i = 0; i <= currentIndex; i++) {
            if (!currentSegments[i].isEmpty()) {
                resultPath.append("/").append(currentSegments[i]);
            }
        }

        // 添加目标路径的剩余部分
        for (int i = targetIndex; i < targetSegments.length; i++) {
            if (!targetSegments[i].isEmpty() && !targetSegments[i].equals(".")) {
                resultPath.append("/").append(targetSegments[i]);
            }
        }

        String resolvedPath = resultPath.length() == 0 ? "/" : resultPath.toString();
        log.debug("解析后的路径: {}", resolvedPath);
        return resolvedPath;
    }

    /**
     * 规范化路径，移除冗余的"."和"//"
     *
     * @param path 原始路径
     * @return 规范化后的路径
     */
    private String normalizePath(String path) {
        // 分割路径
        String[] segments = path.split("/");
        List<String> normalizedSegments = new ArrayList<>();

        // 处理每个路径段
        for (String segment : segments) {
            if (segment.isEmpty() || segment.equals(".")) {
                continue;
            }
            if (segment.equals("..")) {
                if (!normalizedSegments.isEmpty()) {
                    normalizedSegments.remove(normalizedSegments.size() - 1);
                }
                continue;
            }
            normalizedSegments.add(segment);
        }

        // 重建路径
        if (normalizedSegments.isEmpty()) {
            return "/";
        }

        return "/" + String.join("/", normalizedSegments);
    }

    /**
     * 记录组件的数据范围
     */
    public void recordComponentRange(String componentPath, int startIndex, int length, LengthUnit unit) {
        componentRanges.put(componentPath, new ByteRange(startIndex, length, unit));
        log.debug("记录组件数据范围: {}, 起始位置: {}, 长度: {} {}",
                componentPath, startIndex, length, unit);
    }

    /**
     * 获取指定组件的数据
     *
     * @param componentPath 组件路径
     * @return 组件的字节数据
     */
    public byte[] getComponentData(String componentPath) {
        ByteRange range = componentRanges.get(componentPath);
        if (range == null) {
            throw new IllegalArgumentException("未找到组件数据: " + componentPath);
        }

        byte[] data = new byte[range.length];
        encodedData.getBytes(range.startIndex, data);
        return data;
    }

    /**
     * 设置当前处理的节点路径
     */
    public void setCurrentPath(String path) {
        this.currentPath = path;
        log.debug("设置当前处理路径: {}", path);
    }

    /**
     * 获取当前处理的节点路径
     */
    public String getCurrentPath() {
        return currentPath;
    }

    private static class ByteRange {

        final int startIndex;
        final int length;
        final LengthUnit lengthUnit;

        ByteRange(int startIndex, int length, LengthUnit lengthUnit) {
            this.startIndex = startIndex;
            this.length = length;
            this.lengthUnit = lengthUnit;
        }

        public int getLengthInBytes() {
            return lengthUnit == LengthUnit.BIT ? (length + 7) / 8 : length;
        }
    }
} 