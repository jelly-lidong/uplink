package com.satellite.protocol.core.factory;

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
import com.satellite.protocol.core.handler.NodeHandler;
import com.satellite.protocol.core.handler.impl.PaddingNodeHandler;
import com.satellite.protocol.core.handler.impl.StringNodeHandler;
import com.satellite.protocol.core.handler.impl.TimestampNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import java.util.EnumMap;
import java.util.Map;

/**
 * 节点处理器工厂
 */
public class NodeHandlerFactory {
    private static final Map<NodeType, NodeHandler> handlers = new EnumMap<>(NodeType.class);
    
    static {
        handlers.put(NodeType.INT, new IntNodeHandler());
        handlers.put(NodeType.FLOAT, new FloatNodeHandler());
        handlers.put(NodeType.DOUBLE, new DoubleNodeHandler());
        handlers.put(NodeType.HEX, new HexNodeHandler());
        handlers.put(NodeType.BIT, new BitNodeHandler());
        handlers.put(NodeType.ENUM, new EnumNodeHandler());
        handlers.put(NodeType.TIMESTAMP, new TimestampNodeHandler());
        handlers.put(NodeType.DYNAMIC, new DynamicNodeHandler());
        handlers.put(NodeType.PADDING, new PaddingNodeHandler());
        handlers.put(NodeType.CRC16, new Crc16NodeHandler());
        handlers.put(NodeType.CRC32, new Crc32NodeHandler());
        handlers.put(NodeType.STRING, new StringNodeHandler());
    }
    
    public static NodeHandler getHandler(Node node) throws ProtocolException {
        if (node == null || node.getType() == null) {
            throw new ProtocolException("Invalid node or node type");
        }
        
        NodeHandler handler = handlers.get(node.getType());
        if (handler == null) {
            throw new ProtocolException("No handler found for node type: " + node.getType());
        }
        
        return handler;
    }
    
    public static void registerHandler(NodeType type, NodeHandler handler) {
        handlers.put(type, handler);
    }
} 