package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;

public class PaddingNodeHandler extends AbstractNodeHandler {
    
    public PaddingNodeHandler() {
        super(NodeType.PADDING);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        return new byte[node.getByteLength()];
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        return null;
    }
} 