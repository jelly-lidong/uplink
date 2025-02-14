package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;
import java.nio.ByteBuffer;

public class DoubleNodeHandler extends AbstractNodeHandler {
    
    public DoubleNodeHandler() {
        super(NodeType.DOUBLE);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        double doubleValue;
        if (value instanceof Number) {
            doubleValue = ((Number) value).doubleValue();
        } else {
            throw new ProtocolException("Invalid value type for double node: " + value.getClass());
        }
        
        return ByteBuffer.allocate(8).putDouble(doubleValue).array();
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        return ByteBuffer.wrap(bytes, offset, 8).getDouble();
    }
} 