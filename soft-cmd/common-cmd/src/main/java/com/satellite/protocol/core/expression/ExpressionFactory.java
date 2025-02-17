package com.satellite.protocol.core.expression;


public class ExpressionFactory {
    
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
    
    public static Expression createExpression(String expression) {
        // 函数表达式
        if (expression.contains("(")) {
            return new FunctionExpression(expression);
        }
        // ... 其他表达式处理 ...
        return null; // Placeholder return, actual implementation needed
    }
} 