package com.satellite.protocol.core.expression;

import com.satellite.protocol.model.Node;
import java.util.Map;
import java.util.HashMap;
import com.satellite.protocol.core.context.ProtocolContext;

/**
 * 表达式上下文 - 提供表达式执行所需的上下文信息
 */
public interface ExpressionContext {
    /**
     * 获取协议上下文
     * @return 协议上下文
     */
    ProtocolContext getProtocolContext();
    
    Node getCurrentNode();
    
    void setVariable(String name, Object value);
    
    Object getVariable(String name);
}

