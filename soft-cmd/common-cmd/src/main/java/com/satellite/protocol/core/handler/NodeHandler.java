package com.satellite.protocol.core.handler;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;

/**
 * 节点处理器接口 - 策略接口
 */
public interface NodeHandler {

  NodeType getType();

  byte[] encode(Node node, Object value) throws ProtocolException;

  Object decode(Node node, byte[] bytes, int offset) throws ProtocolException;
} 