package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;

public class EnumNodeHandler extends AbstractNodeHandler {
    
    public EnumNodeHandler() {
        super(NodeType.ENUM);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        int enumValue;
        if (value instanceof Enum<?>) {
            enumValue = ((Enum<?>) value).ordinal();
        } else if (value instanceof Number) {
            enumValue = ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                enumValue = Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                throw new ProtocolException("Invalid enum value: " + value);
            }
        } else {
            throw new ProtocolException("Invalid value type for enum node: " + value.getClass());
        }
        
        byte[] bytes = new byte[node.getByteLength()];
        bytes[0] = (byte) enumValue;
        return bytes;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        return bytes[offset] & 0xFF;
    }
} 