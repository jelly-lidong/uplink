package com.satellite.protocol.core.function;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpressionUtils {
    
    /**
     * 解析函数参数
     * 支持: 嵌套函数调用、引号字符串、变量引用
     * 例如: func('abc', ${var}, sum(1, 2))
     */
    public static String[] parseParams(String expression, String funcName) {
        // 去除函数名和括号
        String params = expression.substring(funcName.length() + 1, expression.length() - 1);
        if (params.trim().isEmpty()) {
            return new String[0];
        }
        
        List<String> paramList = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int bracketCount = 0;
        
        for (char c : params.toCharArray()) {
            if (c == '\'') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (inQuotes) {
                current.append(c);
            } else if (c == '(') {
                bracketCount++;
                current.append(c);
            } else if (c == ')') {
                bracketCount--;
                current.append(c);
            } else if (c == ',' && bracketCount == 0) {
                // 参数分隔符
                if (current.length() > 0) {
                    paramList.add(current.toString().trim());
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            paramList.add(current.toString().trim());
        }
        
        log.info("解析函数参数: {} -> {}", expression, paramList);
        return paramList.toArray(new String[0]);
    }
    
    /**
     * 移除字符串两端的引号
     */
    public static String stripQuotes(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("^'|'$", "");
    }
    
    /**
     * 检查是否是数值
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 解析数值
     */
    public static Number parseNumber(String str) {
        if (str == null) {
            throw new IllegalArgumentException("数值不能为null");
        }
        try {
            double value = Double.parseDouble(str);
            // 如果是整数，返回Integer
            if (value == (int) value) {
                return (int) value;
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的数值: " + str);
        }
    }
} 