package com.satellite.protocol.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertExpressionEvaluator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<String, Object> expressionCache = new HashMap<>();
    
    /**
     * 计算convert表达式的值
     * @param expression convert表达式
     * @param value 原始值
     * @return 计算结果
     */
    public static Object evaluate(String expression, Object value) {
        if (expression == null || expression.trim().isEmpty()) {
            return value;
        }

        // 检查缓存
        String cacheKey = generateCacheKey(expression, value);
        if (expressionCache.containsKey(cacheKey)) {
            return expressionCache.get(cacheKey);
        }

        Object result;

        // 处理时间戳转换
        if (isTimeExpression(expression)) {
            result = handleTimeExpression(expression, value.toString());
        }
        // 处理条件表达式
        else if (isConditionalExpression(expression)) {
            result = handleConditionalExpression(expression, value);
        }
        // 处理位运算
        else if (isBitOperation(expression)) {
            result = handleBitOperation(expression, value);
        }
        // 处理数值运算
        else if (isArithmeticOperation(expression)) {
            result = handleArithmeticOperation(expression, value);
        }
        else {
            throw new IllegalArgumentException("不支持的转换表达式: " + expression);
        }

        // 存入缓存
        expressionCache.put(cacheKey, result);
        return result;
    }

    /**
     * 处理时间表达式
     * 支持：
     * - day(x - '2020-01-01 00:00:00') ±1
     * - millSeconds(x - 'currentDate') ±1
     * - seconds(x - 'currentDate') ±1
     */
    private static Object handleTimeExpression(String expression, String timeStr) {
        if (expression.startsWith("day(")) {
            return handleDayExpression(expression, timeStr);
        } else if (expression.startsWith("millSeconds(")) {
            return handleMillSecondsExpression(expression, timeStr);
        } else if (expression.startsWith("seconds(")) {
            return handleSecondsExpression(expression, timeStr);
        }
        throw new IllegalArgumentException("不支持的时间表达式: " + expression);
    }

    private static long handleDayExpression(String expression, String timeStr) {
        // 解析表达式：day(x - '2020-01-01 00:00:00') ±1
        Pattern pattern = Pattern.compile("day\\(x\\s*-\\s*'([^']+)'\\)\\s*([+-])\\s*(\\d+)");
        Matcher matcher = pattern.matcher(expression);
        
        if (matcher.find()) {
            String baseTimeStr = matcher.group(1);
            String operator = matcher.group(2);
            int offset = Integer.parseInt(matcher.group(3));

            LocalDateTime baseTime = LocalDateTime.parse(baseTimeStr, DATE_FORMATTER);
            LocalDateTime targetTime = LocalDateTime.parse(timeStr, DATE_FORMATTER);
            
            long days = ChronoUnit.DAYS.between(baseTime, targetTime);
            return operator.equals("+") ? days + offset : days - offset;
        }
        
        throw new IllegalArgumentException("无效的天数表达式: " + expression);
    }

    private static long handleMillSecondsExpression(String expression, String timeStr) {
        // 处理毫秒级时间差
        if (expression.contains("currentDate")) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetTime = LocalDateTime.parse(timeStr, DATE_FORMATTER);
            return ChronoUnit.MILLIS.between(targetTime, now);
        }
        throw new IllegalArgumentException("无效的毫秒表达式: " + expression);
    }

    private static long handleSecondsExpression(String expression, String timeStr) {
        // 处理秒级时间差
        if (expression.contains("currentDate")) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetTime = LocalDateTime.parse(timeStr, DATE_FORMATTER);
            return ChronoUnit.SECONDS.between(targetTime, now);
        }
        throw new IllegalArgumentException("无效的秒数表达式: " + expression);
    }

    /**
     * 处理条件表达式
     * 支持：if(x>80) then 'HIGH' else if(x>50) then 'MEDIUM' else 'LOW'
     */
    private static Object handleConditionalExpression(String expression, Object value) {
        Pattern pattern = Pattern.compile("if\\((.*?)\\)\\s*then\\s*'([^']*)'(?:\\s*else\\s*(.*))?");
        Matcher matcher = pattern.matcher(expression);
        
        if (matcher.find()) {
            String condition = matcher.group(1);
            String thenValue = matcher.group(2);
            String elseExpression = matcher.group(3);

            if (evaluateCondition(condition, value)) {
                return thenValue;
            } else if (elseExpression != null) {
                if (elseExpression.startsWith("if")) {
                    return handleConditionalExpression(elseExpression, value);
                } else {
                    return elseExpression.replace("'", "");
                }
            }
        }
        
        throw new IllegalArgumentException("无效的条件表达式: " + expression);
    }

    /**
     * 处理位运算
     * 支持：x >> 2, x << 2
     */
    private static Object handleBitOperation(String expression, Object value) {
        Pattern pattern = Pattern.compile("x\\s*([><]{2})\\s*(\\d+)");
        Matcher matcher = pattern.matcher(expression);
        
        if (matcher.find()) {
            String operator = matcher.group(1);
            int bits = Integer.parseInt(matcher.group(2));
            int numValue = Integer.parseInt(value.toString());
            
            return operator.equals(">>") ? numValue >> bits : numValue << bits;
        }
        
        throw new IllegalArgumentException("无效的位运算表达式: " + expression);
    }

    /**
     * 处理数值运算
     * 支持：x * 0.001, (x * 0.1) + 273.15
     */
    private static Object handleArithmeticOperation(String expression, Object value) {
        // 替换x为实际值
        String expr = expression.replace("x", value.toString());
        
        try {
            // 使用简单的表达式计算器
            return evaluateArithmetic(expr);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的算术表达式: " + expression, e);
        }
    }

    private static boolean evaluateCondition(String condition, Object value) {
        Pattern pattern = Pattern.compile("x\\s*([><=]+)\\s*(\\d+)");
        Matcher matcher = pattern.matcher(condition);
        
        if (matcher.find()) {
            String operator = matcher.group(1);
            double compareValue = Double.parseDouble(matcher.group(2));
            double actualValue = Double.parseDouble(value.toString());
            
            switch (operator) {
                case ">": return actualValue > compareValue;
                case "<": return actualValue < compareValue;
                case ">=": return actualValue >= compareValue;
                case "<=": return actualValue <= compareValue;
                case "==": return actualValue == compareValue;
                default: throw new IllegalArgumentException("不支持的比较运算符: " + operator);
            }
        }
        
        throw new IllegalArgumentException("无效的条件: " + condition);
    }

    private static double evaluateArithmetic(String expression) {
        // 简单的算术表达式计算器
        // 这里可以使用更复杂的表达式解析器，如果需要的话
        return Double.parseDouble(expression);
    }

    private static boolean isTimeExpression(String expression) {
        return expression.startsWith("day(") || 
               expression.startsWith("millSeconds(") || 
               expression.startsWith("seconds(");
    }

    private static boolean isConditionalExpression(String expression) {
        return expression.startsWith("if(");
    }

    private static boolean isBitOperation(String expression) {
        return expression.contains(">>") || expression.contains("<<");
    }

    private static boolean isArithmeticOperation(String expression) {
        return expression.matches(".*[+\\-*/].*");
    }

    private static String generateCacheKey(String expression, Object value) {
        return expression + ":" + value.toString();
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        expressionCache.clear();
    }
} 