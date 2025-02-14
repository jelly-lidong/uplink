package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;

public class CrcNodeHandler extends AbstractNodeHandler {
    
    public CrcNodeHandler() {
        super(NodeType.CRC16); // 或者创建一个新的NodeType.CRC
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {

        
        int crcValue;
        if (value instanceof Number) {
            crcValue = ((Number) value).intValue();
        } else {
            throw new ProtocolException("Invalid CRC value type: " + value.getClass());
        }
        
        byte[] result = new byte[node.getByteLength()];
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = (byte)(crcValue & 0xFF);
            crcValue >>>= 8;
        }
        return result;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        int crcValue = 0;
        for (int i = 0; i < node.getByteLength(); i++) {
            crcValue = (crcValue << 8) | (bytes[offset + i] & 0xFF);
        }
        return crcValue;
    }
} 