package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;

public class Crc16NodeHandler extends AbstractNodeHandler {
    
    public Crc16NodeHandler() {
        super(NodeType.CRC16);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (!(value instanceof byte[])) {
            throw new ProtocolException("CRC16 calculation requires byte array input");
        }
        
        byte[] input = (byte[]) value;
        int crc = calculateCRC16(input);
        
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (crc & 0xFF);
        bytes[1] = (byte) ((crc >> 8) & 0xFF);
        return bytes;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        return ((bytes[offset + 1] & 0xFF) << 8) | (bytes[offset] & 0xFF);
    }
    
    private int calculateCRC16(byte[] bytes) {
        int crc = 0xFFFF;
        for (byte b : bytes) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xFFFF;
            crc ^= (b & 0xFF);
            crc ^= ((crc & 0xFF) >> 4);
            crc ^= (crc << 12) & 0xFFFF;
            crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
        }
        return crc & 0xFFFF;
    }
} 