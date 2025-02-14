package com.satellite.protocol.core.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionFactory {
    private static final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
    
    public static Expression getExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return new SimpleExpression(expression);
        }
        
        // 去除空格
        expression = expression.trim();
        
        // 引用表达式
        if (expression.startsWith("${")) {
            return new ReferenceExpression(expression);
        }
        
        // 函数表达式
        if (expression.contains("(")) {
            return new FunctionExpression(expression);
        }
        
        // 算术表达式
        if (expression.matches(".*[-+*/].*")) {
            return new ArithmeticExpression(expression);
        }
        
        // 简单表达式
        return new SimpleExpression(expression);
    }
    
    private static Expression createExpression(String expression) {
        // 根据表达式创建对应的Expression实现
        if (expression.startsWith("${")) {
            return new ReferenceExpression(expression);
        } else if (expression.contains("(")) {
            return new FunctionExpression(expression);
        }
        return new SimpleExpression(expression);
    }
} 