package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;
import java.util.zip.CRC32;

public class Crc32NodeHandler extends AbstractNodeHandler {
    
    public Crc32NodeHandler() {
        super(NodeType.CRC32);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (!(value instanceof byte[])) {
            throw new ProtocolException("CRC32 calculation requires byte array input");
        }
        
        byte[] input = (byte[]) value;
        CRC32 crc32 = new CRC32();
        crc32.update(input);
        long crcValue = crc32.getValue();
        
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) ((crcValue >> (8 * i)) & 0xFF);
        }
        return bytes;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        long crc = 0;
        for (int i = 0; i < 4; i++) {
            crc |= ((long) (bytes[offset + i] & 0xFF)) << (8 * i);
        }
        return crc;
    }
} 