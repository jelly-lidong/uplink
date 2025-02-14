package com.satellite.protocol.core.expression;

import com.satellite.protocol.core.ProtocolException;

public interface Expression {
    Object evaluate(Object value, ExpressionContext context) throws ProtocolException;
} 