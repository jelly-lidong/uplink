package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;

public class DynamicNodeHandler extends AbstractNodeHandler {

  public DynamicNodeHandler() {
    super(NodeType.DYNAMIC);
  }

  @Override
  public byte[] encode(Node node, Object value) throws ProtocolException {
    validateNode(node);
    if (value == null) {
      return new byte[node.getByteLength()];
    }

    // 动态节点的处理逻辑需要根据具体业务来实现
    throw new UnsupportedOperationException("Dynamic node handling not implemented");
  }

  @Override
  public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
    validateNode(node);
    // 动态节点的处理逻辑需要根据具体业务来实现
    throw new UnsupportedOperationException("Dynamic node handling not implemented");
  }
}