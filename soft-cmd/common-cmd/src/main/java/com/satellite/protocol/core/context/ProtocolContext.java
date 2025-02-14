package com.satellite.protocol.core.context;

import com.satellite.protocol.core.composite.ProtocolComponent;
import com.satellite.protocol.model.Node;
import java.lang.StringBuilder;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * 协议上下文 - 处理节点间的依赖关系
 * 存储节点值并提供节点间的值引用能力
 */
@Slf4j
public class ProtocolContext {
    /** 存储节点值的Map */
    private final Map<String, Object> nodeValues = new ConcurrentHashMap<>();
    
    /** 存储待处理的依赖节点 */
    private final Map<String, DependentNode> pendingNodes = new HashMap<>();
    
    /** 当前处理的组件树根节点 */
    private final ProtocolComponent rootComponent;
    
    public ProtocolContext(ProtocolComponent rootComponent) {
        this.rootComponent = rootComponent;
    }
    
    /**
     * 存储节点值
     * @param path 节点完整路径
     * @param value 值
     */
    public void putNodeValue(String path, Object value) {
        log.debug("存储节点值, 路径: {}, 值: {}", path, value);
        nodeValues.put(path, value);
        processPendingNodes(path);
    }
    
    /**
     * 获取节点值
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
     * @param node 依赖节点
     * @param dependencyKeys 依赖的节点key列表
     */
    public void addPendingNode(Node node, String... dependencyKeys) {
        String nodeKey = generateNodeKey(node);
        log.debug("添加待处理节点: {}, 依赖节点: {}", nodeKey, String.join(",", dependencyKeys));
        pendingNodes.put(nodeKey, new DependentNode(node, dependencyKeys));
    }
    
    /**
     * 处理待处理节点
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
     * @param dependentNode 待处理的依赖节点
     */
    private void processNode(DependentNode dependentNode) {
        Node node = dependentNode.getNode();
        String refValue = node.getRefValue();
        
        // 计算表达式的值
        Object value = evaluateExpression(refValue, dependentNode.getDependencyKeys());
        
        // 更新节点值
        node.setValue(value);
        
        // 将节点值存入上下文
        String nodePath = generateNodeKey(node);
        putNodeValue(nodePath, value);
        
        log.debug("更新节点值: {}, 值: {}", nodePath, value);
    }
    
    /**
     * 计算表达式的值
     * @param expression 表达式
     * @param dependencyKeys 依赖节点的key列表
     * @return 计算结果
     */
    private Object evaluateExpression(String expression, String[] dependencyKeys) {
        // 替换表达式中的节点引用为实际值
        String evaluatedExpression = expression;
        for (String key : dependencyKeys) {
            Object value = nodeValues.get(key);
            if (value == null) {
                throw new IllegalStateException("依赖节点值不存在: " + key);
            }
            
            // 替换${key}为实际值
            String placeholder = "\\$\\{" + key.replaceAll("/", "\\\\/") + "\\}";
            evaluatedExpression = evaluatedExpression.replaceAll(placeholder, value.toString());
        }
        
        log.debug("计算表达式: {} -> {}", expression, evaluatedExpression);
        
        // 处理不同的表达式类型
        if (evaluatedExpression.endsWith(".value")) {
            return extractValue(evaluatedExpression);
        } else if (evaluatedExpression.endsWith(".length")) {
            return extractLength(evaluatedExpression);
        } else if (evaluatedExpression.contains(" - ") || evaluatedExpression.contains(" + ")) {
            return calculateArithmetic(evaluatedExpression);
        } else {
            return evaluatedExpression; // 直接返回替换后的值
        }
    }
    
    /**
     * 提取节点的值
     */
    private Object extractValue(String expression) {
        // 移除.value后缀
        String valueStr = expression.substring(0, expression.length() - 6);
        return parseValue(valueStr);
    }
    
    /**
     * 提取节点的长度
     */
    private int extractLength(String expression) {
        // 移除.length后缀
        String valueStr = expression.substring(0, expression.length() - 7);
        Object value = parseValue(valueStr);
        
        if (value instanceof String) {
            return ((String) value).length();
        } else if (value instanceof byte[]) {
            return ((byte[]) value).length;
        } else if (value instanceof Collection) {
            return ((Collection<?>) value).size();
        } else {
            throw new IllegalArgumentException("无法获取长度: " + valueStr);
        }
    }
    
    /**
     * 计算算术表达式
     */
    private Object calculateArithmetic(String expression) {
        // 分割表达式
        String[] parts = expression.split("\\s+");
        if (parts.length != 3) { // 简单的二元运算
            throw new IllegalArgumentException("不支持的算术表达式: " + expression);
        }
        
        Object leftValue = parseValue(parts[0]);
        String operator = parts[1];
        Object rightValue = parseValue(parts[2]);
        
        // 确保操作数是数值类型
        if (!(leftValue instanceof Number) || !(rightValue instanceof Number)) {
            throw new IllegalArgumentException("算术操作数必须是数值类型");
        }
        
        double left = ((Number) leftValue).doubleValue();
        double right = ((Number) rightValue).doubleValue();
        
        // 执行运算
        double result;
        switch (operator) {
            case "+":
                result = left + right;
                break;
            case "-":
                result = left - right;
                break;
            default:
                throw new IllegalArgumentException("不支持的运算符: " + operator);
        }
        
        // 如果结果是整数，返回Integer
        if (result == (int) result) {
            return (int) result;
        }
        return result;
    }
    
    /**
     * 解析值字符串
     */
    private Object parseValue(String valueStr) {
        try {
            return Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(valueStr);
            } catch (NumberFormatException e2) {
                return valueStr;
            }
        }
    }
    
    /**
     * 生成节点的唯一key
     * @param node 节点
     * @return 节点key
     */
    private String generateNodeKey(Node node) {
        return node.getName(); // 可以根据需要使用更复杂的key生成策略
    }
    
    /**
     * 清理上下文
     */
    public void clear() {
        nodeValues.clear();
        pendingNodes.clear();
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
     * @param currentPath 当前节点路径
     * @param targetPath 目标路径(可以是相对路径或绝对路径)
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
} 