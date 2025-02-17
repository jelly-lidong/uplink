package com.satellite.protocol.core.expression;

import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.model.Node;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * 默认的表达式上下文实现
 */
public class DefaultExpressionContext implements ExpressionContext {
  private final Node                currentNode;
  private final Map<String, Object> variables;

  public DefaultExpressionContext(Node currentNode) {
    this.currentNode = currentNode;
    this.variables = new HashMap<>();
  }

  @Override
  public Node getCurrentNode() {
    return currentNode;
  }

  @Override
  public void setVariable(String name, Object value) {
    variables.put(name, value);
  }

  @Override
  public Object getVariable(String name) {
    return variables.get(name);
  }

  @Override
  public ProtocolContext getProtocolContext() {
    // Implementation needed
    throw new UnsupportedOperationException("Method not implemented");
  }
}