package com.satellite.protocol.core.expression;

import com.satellite.protocol.core.ProtocolException;

/**
 * 简单表达式处理器，用于处理直接值
 */
public class SimpleExpression implements Expression {
    private final String value;
    
    public SimpleExpression(String expression) {
        this.value = expression;
    }
    
    @Override
    public Object evaluate(Object value, ExpressionContext context) throws ProtocolException {
        // 如果value不为空，优先使用value
        if (value != null) {
            return value;
        }
        
        // 处理null值
        if (this.value == null || "null".equalsIgnoreCase(this.value)) {
            return null;
        }
        
        // 处理布尔值
        if ("true".equalsIgnoreCase(this.value)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(this.value)) {
            return Boolean.FALSE;
        }
        
        try {
            // 尝试解析为数字
            if (this.value.contains(".")) {
                // 浮点数
                return Double.parseDouble(this.value);
            } else {
                // 整数
                return Long.parseLong(this.value);
            }
        } catch (NumberFormatException e) {
            // 如果不是数字，则作为字符串返回
            return this.value;
        }
    }
} 