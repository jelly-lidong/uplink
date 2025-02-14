package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;

public class HexNodeHandler extends AbstractNodeHandler {
    
    public HexNodeHandler() {
        super(NodeType.HEX);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        String hexStr = value.toString();
        if (hexStr.startsWith("0x")) {
            hexStr = hexStr.substring(2);
        }
        
        byte[] bytes = new byte[node.getByteLength()];
        for (int i = 0; i < hexStr.length() && i/2 < bytes.length; i+=2) {
            String byteStr = hexStr.substring(i, Math.min(i+2, hexStr.length()));
            bytes[i/2] = (byte) Integer.parseInt(byteStr, 16);
        }
        return bytes;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        StringBuilder sb = new StringBuilder("0x");
        for (int i = 0; i < node.getByteLength(); i++) {
            sb.append(String.format("%02X", bytes[offset + i]));
        }
        return sb.toString();
    }
} 