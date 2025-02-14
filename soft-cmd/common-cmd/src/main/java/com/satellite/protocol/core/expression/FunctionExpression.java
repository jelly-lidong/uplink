package com.satellite.protocol.core.expression;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.function.FunctionRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionExpression implements Expression {
    private static final Pattern FUNCTION_PATTERN = 
        Pattern.compile("(\\w+)\\(([^)]+)\\)\\s*([+-]\\s*\\d+)?");
    
    private final String functionName;
    private final String[] arguments;
    private final Integer adjustment;
    
    public FunctionExpression(String expression) {
        Matcher matcher = FUNCTION_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid function expression: " + expression);
        }
        
        this.functionName = matcher.group(1);
        this.arguments = matcher.group(2).split("\\s*,\\s*");
        this.adjustment = matcher.group(3) != null ? 
            Integer.parseInt(matcher.group(3).replaceAll("\\s", "")) : 0;
    }
    
    @Override
    public Object evaluate(Object value, ExpressionContext context) throws ProtocolException {
        // 获取函数实例
        Function function = FunctionRegistry.getFunction(functionName);
        if (function == null) {
            throw new ProtocolException("Unknown function: " + functionName);
        }
        
        // 准备函数参数
        List<Object> args = new ArrayList<>();
        for (String arg : arguments) {
            if (arg.equals("x")) {
                args.add(value);
            } else {
                args.add(arg);
            }
        }
        
        // 执行函数
        Object result = function.execute(args.toArray());
        
        // 应用调整值
        if (result instanceof Number) {
            return ((Number) result).longValue() + adjustment;
        }
        
        return result;
    }
} 