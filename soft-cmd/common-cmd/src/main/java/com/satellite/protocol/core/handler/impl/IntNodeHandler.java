package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;

/**
 * 整数节点处理器
 */
public class IntNodeHandler extends AbstractNodeHandler {
    
    public IntNodeHandler() {
        super(NodeType.INT);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        long longValue;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        } else {
            throw new ProtocolException("Invalid value type for int node: " + value.getClass());
        }
        
        byte[] bytes = new byte[node.getByteLength()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (longValue >> (8 * i));
        }
        return bytes;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        long value = 0;
        for (int i = 0; i < node.getByteLength(); i++) {
            value |= ((long) (bytes[offset + i] & 0xFF)) << (8 * i);
        }
        return value;
    }
} 