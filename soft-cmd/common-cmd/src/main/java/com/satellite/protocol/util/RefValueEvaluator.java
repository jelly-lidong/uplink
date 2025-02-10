package com.satellite.protocol.util;

import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.Protocol;
import com.satellite.protocol.model.ProtocolBody;
import com.satellite.protocol.model.ProtocolHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefValueEvaluator {
    // 缓存已计算的长度值
    private static final Map<String, Integer> lengthCache = new HashMap<>();
    
    /**
     * 计算refValue表达式的值
     * @param refValue refValue表达式
     * @param currentNode 当前节点
     * @param protocol 协议对象
     * @return 计算结果
     */
    public static int evaluate(String refValue, Node currentNode, Protocol protocol) {
        if (refValue == null || refValue.trim().isEmpty()) {
            throw new IllegalArgumentException("refValue不能为空");
        }

        // 检查缓存
        String cacheKey = generateCacheKey(refValue, currentNode);
        if (lengthCache.containsKey(cacheKey)) {
            return lengthCache.get(cacheKey);
        }

        int result;
        
        // 处理sum函数
        if (refValue.startsWith("sum(") && refValue.endsWith(")")) {
            result = handleSumFunction(refValue, currentNode, protocol);
        }
        // 处理length属性
        else if (refValue.endsWith(".length")) {
            result = handleLengthAttribute(refValue, currentNode, protocol);
        }
        // 处理算术表达式
        else {
            result = handleArithmeticExpression(refValue);
        }

        // 存入缓存
        lengthCache.put(cacheKey, result);
        return result;
    }

    /**
     * 处理sum函数
     * 例如：sum(../二级协议体.length)
     */
    private static int handleSumFunction(String expression, Node currentNode, Protocol protocol) {
        // 提取sum函数内的表达式
        String innerExpression = expression.substring(4, expression.length() - 1);
        
        // 处理路径表达式
        if (innerExpression.startsWith("../")) {
            String[] paths = innerExpression.split("/");
            return calculatePathLength(paths, currentNode, protocol);
        }
        
        throw new IllegalArgumentException("不支持的sum表达式: " + expression);
    }

    /**
     * 处理length属性
     * 例如：../二级协议体.length
     */
    private static int handleLengthAttribute(String expression, Node currentNode, Protocol protocol) {
        String[] paths = expression.split("\\.");
        return calculatePathLength(paths, currentNode, protocol);
    }

    /**
     * 处理算术表达式
     * 例如：x + 1, x - 1
     */
    private static int handleArithmeticExpression(String expression) {
        // 使用正则表达式解析算术表达式
        Pattern pattern = Pattern.compile("(\\d+)\\s*([+\\-*/])\\s*(\\d+)");
        Matcher matcher = pattern.matcher(expression);
        
        if (matcher.find()) {
            int num1 = Integer.parseInt(matcher.group(1));
            String operator = matcher.group(2);
            int num2 = Integer.parseInt(matcher.group(3));
            
            switch (operator) {
                case "+": return num1 + num2;
                case "-": return num1 - num2;
                case "*": return num1 * num2;
                case "/": return num1 / num2;
                default: throw new IllegalArgumentException("不支持的运算符: " + operator);
            }
        }
        
        throw new IllegalArgumentException("无效的算术表达式: " + expression);
    }

    /**
     * 计算路径指向的长度
     */
    private static int calculatePathLength(String[] paths, Node currentNode, Protocol protocol) {
        ProtocolBody currentBody = findProtocolBody(paths, protocol);
        if (currentBody == null) {
            throw new IllegalArgumentException("找不到指定的协议体: " + String.join("/", paths));
        }
        
        // 计算协议体的总长度
        return calculateBodyLength(currentBody);
    }

    /**
     * 根据路径查找协议体
     */
    private static ProtocolBody findProtocolBody(String[] paths, Protocol protocol) {
        ProtocolBody currentBody = protocol.getBody();
        
        for (String path : paths) {
            if (path.equals("..")) {
                // 向上一级
                if (currentBody != null) {
                    currentBody = currentBody.getSubBody();
                }
            } else if (!path.equals("length")) {
                // 查找指定名称的协议体
                while (currentBody != null && !currentBody.getName().equals(path)) {
                    currentBody = currentBody.getSubBody();
                }
            }
        }
        
        return currentBody;
    }

    /**
     * 计算协议体的总长度
     */
    private static int calculateBodyLength(ProtocolBody body) {
        int totalLength = 0;
        
        // 计算header长度
        if (body.getHeader() != null) {
            totalLength += calculateHeaderLength(body.getHeader());
        }
        
        // 计算subBody长度
        if (body.getSubBody() != null) {
            totalLength += calculateBodyLength(body.getSubBody());
        }
        
        // 计算check长度
        if (body.getCheck() != null) {
            totalLength += body.getCheck().getLength();
        }
        
        return totalLength;
    }

    /**
     * 计算协议头的长度
     */
    private static int calculateHeaderLength(ProtocolHeader header) {
        return header.getLength();
    }

    /**
     * 生成缓存key
     */
    private static String generateCacheKey(String refValue, Node currentNode) {
        return currentNode.getName() + ":" + refValue;
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        lengthCache.clear();
    }
} 