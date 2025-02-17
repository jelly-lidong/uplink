package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;

public class BitNodeHandler extends AbstractNodeHandler {
    
    public BitNodeHandler() {
        super(NodeType.BIT);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        boolean bitValue;
        if (value instanceof Boolean) {
            bitValue = (Boolean) value;
        } else if (value instanceof Number) {
            bitValue = ((Number) value).intValue() != 0;
        } else {
            throw new ProtocolException("Invalid value type for bit node: " + value.getClass()+ ", value: " + value);
        }
        
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (bitValue ? 1 : 0);
        return bytes;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        return (bytes[offset] & 0x01) != 0;
    }
}