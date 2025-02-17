package com.satellite.protocol.core.function.impl;

import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.context.ProtocolContext;
import lombok.extern.slf4j.Slf4j;
import com.satellite.protocol.core.function.ExpressionUtils;

@Slf4j
public class StringFunction implements Function {
    @Override
    public String getName() {
        return "string";
    }
    
    @Override
    public boolean matches(String expression) {
        return expression.startsWith("substr(") || 
               expression.startsWith("concat(") ||
               expression.startsWith("replace(") ||
               expression.startsWith("trim(");
    }
    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        if (expression.startsWith("substr(")) {
            return executeSubstr(expression);
        } else if (expression.startsWith("concat(")) {
            return executeConcat(expression);
        } else if (expression.startsWith("replace(")) {
            return executeReplace(expression);
        } else if (expression.startsWith("trim(")) {
            return executeTrim(expression);
        }
        throw new IllegalArgumentException("不支持的字符串操作: " + expression);
    }
    
    private String executeSubstr(String expression) {
        String[] params = ExpressionUtils.parseParams(expression, "substr");
        if (params.length != 3) {
            throw new IllegalArgumentException("substr需要3个参数: " + expression);
        }
        
        String str = ExpressionUtils.stripQuotes(params[0]);
        int start = ExpressionUtils.parseNumber(params[1]).intValue();
        int length = ExpressionUtils.parseNumber(params[2]).intValue();
        
        return str.substring(start, start + length);
    }
    
    private String executeConcat(String expression) {
        String[] params = ExpressionUtils.parseParams(expression, "concat");
        
        StringBuilder result = new StringBuilder();
        for (String param : params) {
            result.append(ExpressionUtils.stripQuotes(param));
        }
        return result.toString();
    }
    
    private String executeReplace(String expression) {
        // replace('hello world', 'world', 'java') -> 'hello java'
        String params = expression.substring(8, expression.length() - 1);
        String[] parts = params.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("replace需要3个参数: " + expression);
        }
        
        String str = parts[0].trim().replaceAll("'", "");
        String oldStr = parts[1].trim().replaceAll("'", "");
        String newStr = parts[2].trim().replaceAll("'", "");
        
        return str.replace(oldStr, newStr);
    }
    
    private String executeTrim(String expression) {
        // trim('  hello  ') -> 'hello'
        String params = expression.substring(5, expression.length() - 1);
        return params.trim().replaceAll("'", "");
    }
} 