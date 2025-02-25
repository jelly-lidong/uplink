package com.satellite.protocol.core.expression;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.function.FunctionRegistry;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FunctionExpression implements Expression {
    // 修改正则表达式以支持嵌套函数
    private static final Pattern FUNCTION_PATTERN = 
        Pattern.compile("(\\w+)\\(([^()]*(?:\\([^()]*\\)[^()]*)*?)\\)\\s*(?:([+-])\\s*(\\d+))?");
    
    private final String functionName;
    private final String[] args;
    private final long adjustment;
    
    public FunctionExpression(String expression) {
        // 解析函数表达式
        Matcher matcher = FUNCTION_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("无效的函数表达式: " + expression);
        }
        
        // 提取函数名
        this.functionName = matcher.group(1);
        
        // 解析参数列表，保持原始形式以支持嵌套函数
        String argsStr = matcher.group(2);
        this.args = splitArguments(argsStr);
        
        // 解析调整值
        String operator = matcher.group(3);
        String adjustStr = matcher.group(4);
        if (operator != null && adjustStr != null) {
            long value = Long.parseLong(adjustStr);
            this.adjustment = operator.equals("+") ? value : -value;
        } else {
            this.adjustment = 0;
        }
        
        log.debug("解析函数表达式: {} -> 函数名: {}, 参数: {}, 调整值: {}", 
            expression, functionName, args, adjustment);
    }
    
    /**
     * 分割函数参数，处理嵌套括号
     */
    private String[] splitArguments(String argsStr) {
        List<String> args = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int parentheses = 0;
        
        for (char c : argsStr.toCharArray()) {
            if (c == '(') {
                parentheses++;
                current.append(c);
            } else if (c == ')') {
                parentheses--;
                current.append(c);
            } else if (c == ',' && parentheses == 0) {
                // 只在最外层处理逗号分隔
                args.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            args.add(current.toString().trim());
        }
        
        return args.toArray(new String[0]);
    }
    
    @Override
    public Object evaluate(Object value, ExpressionContext context) throws ProtocolException {
//        // 获取函数
//        Function function = FunctionRegistry.getFunction(functionName);
//        if (function == null) {
//            throw new ProtocolException("未找到函数: " + functionName);
//        }
//
//        // 构建函数调用表达式，递归处理嵌套函数
//        StringBuilder expression = new StringBuilder(functionName);
//        expression.append("(");
//        for (int i = 0; i < args.length; i++) {
//            if (i > 0) {
//                expression.append(",");
//            }
//
//            String arg = args[i];
//            if ("x".equals(arg)) {
//                // 处理参数占位符
//                expression.append(value);
//            } else if (arg.contains("(")) {
//                // 递归处理嵌套函数
//                FunctionExpression nested = new FunctionExpression(arg);
//                Object nestedResult = nested.evaluate(value, context);
//                expression.append(nestedResult);
//            } else {
//                expression.append(arg);
//            }
//        }
//        expression.append(")");
//
//        // 执行函数
//        Object result = function.execute(expression.toString(), context.getProtocolContext());
//
//        // 应用调整值
//        if (result instanceof Number) {
//            return ((Number) result).longValue() + adjustment;
//        }
        
        return null;
    }
} 