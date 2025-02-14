package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;
import java.time.Instant;

public class TimestampNodeHandler extends AbstractNodeHandler {
    public TimestampNodeHandler() {
        super(NodeType.TIMESTAMP);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        long timestamp;
        if (value instanceof Instant) {
            timestamp = ((Instant) value).getEpochSecond();
        } else if (value instanceof Number) {
            timestamp = ((Number) value).longValue();
        } else {
            throw new ProtocolException("Invalid value type for timestamp node: " + value.getClass());
        }
        
        byte[] bytes = new byte[node.getByteLength()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (timestamp >> (8 * i));
        }
        return bytes;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        long timestamp = 0;
        for (int i = 0; i < node.getByteLength(); i++) {
            timestamp |= ((long) (bytes[offset + i] & 0xFF)) << (8 * i);
        }
        return Instant.ofEpochSecond(timestamp);
    }
} 