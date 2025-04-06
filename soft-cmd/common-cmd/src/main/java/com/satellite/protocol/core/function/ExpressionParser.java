package com.satellite.protocol.core.function;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpressionParser {
    private static final String OPERATORS = "+-*/()";
    
    /**
     * 解析表达式为token列表
     * 支持: 函数调用、算术运算、括号嵌套、变量引用
     */
    public static List<String> parse(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int bracketCount = 0;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // 处理引号内的内容
            if (c == '\'') {
                inQuotes = !inQuotes;
                current.append(c);
                continue;
            }
            
            if (inQuotes) {
                current.append(c);
                continue;
            }
            
            // 处理变量引用 ${...}
            if (c == '$' && i + 1 < expression.length() && expression.charAt(i + 1) == '{') {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                int endBrace = expression.indexOf('}', i);
                if (endBrace == -1) {
                    throw new IllegalArgumentException("变量引用未闭合: " + expression);
                }
                tokens.add(expression.substring(i, endBrace + 1));
                i = endBrace;
                continue;
            }
            
            // 处理括号
            if (c == '(') {
                bracketCount++;
                if (bracketCount == 1 && current.length() > 0) {
                    // 函数名结束
                    current.append(c);
                    continue;
                }
            } else if (c == ')') {
                bracketCount--;
                if (bracketCount == 0 && current.toString().contains("(")) {
                    // 函数调用结束
                    current.append(c);
                    tokens.add(current.toString());
                    current = new StringBuilder();
                    continue;
                }
            }
            
            // 处理运算符
            if (bracketCount == 0 && OPERATORS.indexOf(c) >= 0 && !current.toString().contains("(")) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                if (c != ' ') {
                    tokens.add(String.valueOf(c));
                }
                continue;
            }
            
            // 处理空格
            if (c == ' ' && bracketCount == 0 && !current.toString().contains("(")) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                continue;
            }
            
            // 其他字符
            current.append(c);
        }
        
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        
        log.info("解析表达式: {} -> {}", expression, tokens);
        return tokens;
    }

} 