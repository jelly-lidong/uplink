package com.satellite.protocol.core.expression;

import com.satellite.protocol.core.ProtocolException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArithmeticExpression implements Expression {
    private static final Pattern ARITHMETIC_PATTERN = 
        Pattern.compile("(.+?)\\s*([-+*/])\\s*(.+)");
    
    private final Expression leftExpr;
    private final Expression rightExpr;
    private final String operator;
    
    public ArithmeticExpression(String expression) {
        Matcher matcher = ARITHMETIC_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid arithmetic expression: " + expression);
        }
        
        this.leftExpr = ExpressionFactory.getExpression(matcher.group(1));
        this.rightExpr = ExpressionFactory.getExpression(matcher.group(3));
        this.operator = matcher.group(2);
    }
    
    @Override
    public Object evaluate(Object value, ExpressionContext context) throws ProtocolException {
        Number left = evaluateAsNumber(leftExpr.evaluate(value, context));
        Number right = evaluateAsNumber(rightExpr.evaluate(value, context));
        
        switch (operator) {
            case "+":
                return left.doubleValue() + right.doubleValue();
            case "-":
                return left.doubleValue() - right.doubleValue();
            case "*":
                return left.doubleValue() * right.doubleValue();
            case "/":
                if (right.doubleValue() == 0) {
                    throw new ProtocolException("Division by zero");
                }
                return left.doubleValue() / right.doubleValue();
            default:
                throw new ProtocolException("Unsupported operator: " + operator);
        }
    }
    
    private Number evaluateAsNumber(Object value) throws ProtocolException {
        if (value instanceof Number) {
            return (Number) value;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new ProtocolException("Cannot convert to number: " + value);
        }
    }
} 