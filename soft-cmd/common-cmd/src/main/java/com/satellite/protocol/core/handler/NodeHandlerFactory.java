package com.satellite.protocol.core.handler;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.handler.impl.BitNodeHandler;
import com.satellite.protocol.core.handler.impl.Crc16NodeHandler;
import com.satellite.protocol.core.handler.impl.Crc32NodeHandler;
import com.satellite.protocol.core.handler.impl.DoubleNodeHandler;
import com.satellite.protocol.core.handler.impl.DynamicNodeHandler;
import com.satellite.protocol.core.handler.impl.EnumNodeHandler;
import com.satellite.protocol.core.handler.impl.FloatNodeHandler;
import com.satellite.protocol.core.handler.impl.HexNodeHandler;
import com.satellite.protocol.core.handler.impl.IntNodeHandler;
import com.satellite.protocol.core.handler.impl.PaddingNodeHandler;
import com.satellite.protocol.core.handler.impl.StringNodeHandler;
import com.satellite.protocol.core.handler.impl.TimestampNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import java.util.EnumMap;
import java.util.Map;

/**
 * 节点处理器工厂 - 单例模式
 */
public class NodeHandlerFactory {
    private static final class InstanceHolder {
        private static final NodeHandlerFactory INSTANCE = new NodeHandlerFactory();
    }
    
    private final Map<NodeType, NodeHandler> handlers = new EnumMap<>(NodeType.class);
    
    private NodeHandlerFactory() {
        initHandlers();
    }
    
    public static NodeHandlerFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }
    
    private void initHandlers() {
        // 注册所有内置的处理器
        registerHandler(new IntNodeHandler());
        registerHandler(new FloatNodeHandler());
        registerHandler(new DoubleNodeHandler());
        registerHandler(new HexNodeHandler());
        registerHandler(new BitNodeHandler());
        registerHandler(new EnumNodeHandler());
        registerHandler(new TimestampNodeHandler());
        registerHandler(new DynamicNodeHandler());
        registerHandler(new PaddingNodeHandler());
        registerHandler(new Crc16NodeHandler());
        registerHandler(new Crc32NodeHandler());
        registerHandler(new StringNodeHandler());
    }
    
    /**
     * 获取节点对应的处理器
     */
    public NodeHandler getHandler(Node node) throws ProtocolException {
        if (node == null || node.getType() == null) {
            throw new ProtocolException("Invalid node or node type");
        }
        
        NodeHandler handler = handlers.get(node.getType());
        if (handler == null) {
            throw new ProtocolException("No handler found for node type: " + node.getType());
        }
        
        return handler;
    }
    
    /**
     * 注册新的处理器
     */
    public void registerHandler(NodeHandler handler) {
        if (handler != null) {
            handlers.put(handler.getType(), handler);
        }
    }
    
    /**
     * 移除处理器
     */
    public void removeHandler(NodeType type) {
        handlers.remove(type);
    }
    
    /**
     * 检查是否支持指定类型
     */
    public boolean supportsType(NodeType type) {
        return handlers.containsKey(type);
    }
} 