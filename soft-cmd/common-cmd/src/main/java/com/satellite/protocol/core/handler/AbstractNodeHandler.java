package com.satellite.protocol.core.handler;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;

/**
 * 节点处理器抽象基类
 */
public abstract class AbstractNodeHandler implements NodeHandler {
    private final NodeType type;

    protected AbstractNodeHandler(NodeType type) {
        this.type = type;
    }

    @Override
    public NodeType getType() {
        return type;
    }

    protected void validateNode(Node node) throws ProtocolException {
        if (node == null) {
            throw new ProtocolException("Node cannot be null");
        }
        if (node.getType() != getType()) {
            throw new ProtocolException("Node type mismatch: expected " + getType() + ", but was " + node.getType());
        }
        if (node.getByteLength() <= 0) {
            throw new ProtocolException("Invalid byte length: " + node.getByteLength());
        }
    }
}  