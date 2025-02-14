package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.handler.NodeHandler;
import com.satellite.protocol.core.handler.NodeHandlerFactory;
import com.satellite.protocol.model.Node;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import com.satellite.protocol.core.context.ProtocolContext;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 节点组件 - 叶子节点
 * 负责单个节点的编解码,是组合模式中的叶子节点
 */
@Slf4j
public class NodeComponent implements ProtocolComponent {
    /** 节点对象 */
    private final Node node;
    /** 节点处理器工厂 */
    private final NodeHandlerFactory handlerFactory;
    private final String nodePath; // 添加节点路径字段
    
    /**
     * 构造函数
     * @param node 要处理的节点对象
     * @param parentPath 父节点路径
     */
    public NodeComponent(Node node, String parentPath) {
        this.node = node;
        this.handlerFactory = NodeHandlerFactory.getInstance();
        this.nodePath = parentPath + "/" + node.getName();
        log.debug("创建节点组件: {}, 路径: {}", node.getName(), nodePath);
    }
    
    @Override
    public void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        log.debug("开始编码节点: {}, 类型: {}, 路径: {}", 
            node.getName(), node.getType(), nodePath);
        
        // 检查是否有依赖
        if (hasDependencies(node)) {
            // 如果有依赖且依赖未满足，则加入待处理队列
            if (areDependenciesSatisfied(node, context)) {
                context.addPendingNode(node, getDependencyPaths(node, context));
                log.debug("节点 {} 依赖未满足，加入待处理队列", nodePath);
                return;
            }
        }
        
        // 处理节点
        NodeHandler handler = handlerFactory.getHandler(node);
        byte[] bytes = handler.encode(node, node.getValue());
        buffer.writeBytes(bytes);
        
        // 将节点值存入上下文
        context.putNodeValue(nodePath, node.getValue());
        log.debug("完成节点编码: {}, 值: {}", nodePath, node.getValue());
    }
    
    @Override
    public void decode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        log.debug("开始解码节点: {}, 类型: {}, 路径: {}", 
            node.getName(), node.getType(), nodePath);
        
        // 检查是否有依赖
        if (hasDependencies(node)) {
            // 如果有依赖且依赖未满足，则加入待处理队列
            if (areDependenciesSatisfied(node, context)) {
                context.addPendingNode(node, getDependencyPaths(node, context));
                log.debug("节点 {} 依赖未满足，加入待处理队列", nodePath);
                return;
            }
        }
        
        // 处理节点
        NodeHandler handler = handlerFactory.getHandler(node);
        byte[] bytes = new byte[node.getByteLength()];
        buffer.readBytes(bytes);
        Object value = handler.decode(node, bytes, 0);
        node.setValue(value);
        
        // 将节点值存入上下文
        context.putNodeValue(nodePath, value);
        log.debug("完成节点解码: {}, 值: {}", nodePath, value);
    }
    
    /**
     * 检查节点是否有依赖
     * 通过检查节点的refValue属性判断是否引用了其他节点的值
     */
    private boolean hasDependencies(Node node) {
        String refValue = node.getRefValue();
        // 如果refValue不为空且包含${}格式的节点引用，说明该节点依赖于其他节点
        return refValue != null && refValue.contains("${");
    }
    
    /**
     * 检查节点的依赖是否都满足
     * 根据refValue中引用的节点路径检查依赖节点的值是否存在
     */
    private boolean areDependenciesSatisfied(Node node, ProtocolContext context) {
        String refValue = node.getRefValue();
        String[] nodeRefs = parseNodeRefs(refValue);
        for (String nodeRef : nodeRefs) {
            if (context.getNodeValue(nodeRef) == null) {
                return true; // 依赖未满足，需要加入待处理队列
            }
        }
        return false; // 所有依赖都满足，可以直接处理
    }
    
    /**
     * 解析refValue中引用的节点路径
     * @param refValue 引用值表达式
     * @return 依赖的节点路径数组
     */
    private String[] parseNodeRefs(String refValue) {
        if (refValue == null || refValue.trim().isEmpty()) {
            return new String[0];
        }
        
        Set<String> nodeRefs = new HashSet<>();
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(refValue);
        
        while (matcher.find()) {
            String nodePath = matcher.group(1).trim();
            // 验证路径格式
            if (nodePath.isEmpty()) {
                throw new IllegalArgumentException("节点引用路径不能为空");
            }
            nodeRefs.add(nodePath);
            log.debug("找到节点引用路径: {}", nodePath);
        }
        
        log.debug("解析节点引用: {} -> {}", refValue, nodeRefs);
        return nodeRefs.toArray(new String[0]);
    }
    
    /**
     * 获取依赖节点的路径列表
     */
    private String[] getDependencyKeys(Node node) {
        String refValue = node.getRefValue();
        return parseNodeRefs(refValue);
    }
    
    private String[] getDependencyPaths(Node node, ProtocolContext context) {
        String[] relativePaths = parseNodeRefs(node.getRefValue());
        String[] absolutePaths = new String[relativePaths.length];
        
        for (int i = 0; i < relativePaths.length; i++) {
            absolutePaths[i] = context.resolveNodePath(nodePath, relativePaths[i]);
        }
        
        return absolutePaths;
    }
    
    // 叶子节点不支持子节点操作
    @Override
    public void addChild(ProtocolComponent child) {
        throw new UnsupportedOperationException("Leaf node doesn't support children");
    }
    
    @Override
    public void removeChild(ProtocolComponent child) {
        throw new UnsupportedOperationException("Leaf node doesn't support children");
    }
    
    @Override
    public ProtocolComponent getChild(int index) {
        throw new UnsupportedOperationException("Leaf node doesn't support children");
    }
} 