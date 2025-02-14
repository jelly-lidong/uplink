package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;
import java.nio.charset.StandardCharsets;

public class StringNodeHandler extends AbstractNodeHandler {
    
    public StringNodeHandler() {
        super(NodeType.STRING);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        byte[] strBytes = value.toString().getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[node.getByteLength()];
        System.arraycopy(strBytes, 0, bytes, 0, Math.min(strBytes.length, bytes.length));
        return bytes;
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        // 去掉末尾的0字节
        int length = node.getByteLength();
        while (length > 0 && bytes[offset + length - 1] == 0) {
            length--;
        }
        return new String(bytes, offset, length, StandardCharsets.UTF_8);
    }
} 