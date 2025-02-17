package com.satellite.protocol.core.function;

import com.satellite.protocol.core.function.impl.AviatorFunction;
import com.satellite.protocol.core.function.impl.ChecksumFunction;
import com.satellite.protocol.core.function.impl.Crc16Function;
import com.satellite.protocol.core.function.impl.Crc32Function;
import com.satellite.protocol.core.function.impl.DayFunction;
import com.satellite.protocol.core.function.impl.LengthFunction;
import com.satellite.protocol.core.function.impl.MilliSecondsFunction;
import com.satellite.protocol.core.function.impl.SecondsFunction;
import com.satellite.protocol.core.function.impl.StringFunction;
import com.satellite.protocol.core.function.impl.ValueFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FunctionRegistry {

  private static final Map<String, Function> functions = new HashMap<>();

  static {
    // 注册内置函数
    registerFunction(new LengthFunction());     // 处理 .length 表达式
    registerFunction(new ValueFunction());      // 处理 .value 表达式
    registerFunction(new Crc16Function());      // 处理 crc16() 函数
    registerFunction(new Crc32Function());      // 处理 crc32() 函数
    registerFunction(new ChecksumFunction());   // 处理 checksum() 函数
    registerFunction(new MilliSecondsFunction()); // 处理毫秒差值
    registerFunction(new SecondsFunction());    // 处理秒差值
    registerFunction(new DayFunction());        // 处理天数差值
    registerFunction(new StringFunction());     // 处理字符串操作
    registerFunction(new AviatorFunction()); // 处理算术表达式
  }

  public static void registerFunction(Function function) {
    functions.put(function.getName(), function);
    log.debug("注册函数: {}", function.getName());
  }

  /**
   * 根据表达式获取合适的函数实例
   */
  public static Function getFunction(String name) {
    return functions.get(name);
  }

  /**
   * 评估表达式并返回结果
   */
  public static Object evaluate(String evaluatedExpression, FunctionContext context) {
    log.debug("开始计算表达式: {}", evaluatedExpression);
    
    // 解析表达式为标记
    List<String> tokens = ExpressionParser.parse(evaluatedExpression);
    
    // 使用栈来处理嵌套表达式
    Stack<ExpressionFrame> expressionStack = new Stack<>();
    StringBuilder currentExpression = new StringBuilder();
    int parenthesesCount = 0;
    
    for (String token : tokens) {
        if (token.contains("(")) {
            parenthesesCount++;
            if (parenthesesCount > 1) {
                // 嵌套函数的开始
                currentExpression.append(token).append(" ");
            } else {
                // 外层函数
                String functionName = token.substring(0, token.indexOf('('));
                Function function = getFunction(functionName);
                if (function == null) {
                    throw new IllegalArgumentException("未找到函数: " + functionName);
                }
                expressionStack.push(new ExpressionFrame(function));
            }
        } else if (token.contains(")")) {
            parenthesesCount--;
            currentExpression.append(token).append(" ");
            
            if (parenthesesCount == 0) {
                // 函数执行完成
                ExpressionFrame frame = expressionStack.pop();
                String funcExpression = currentExpression.toString().trim();
                
                // 如果是嵌套函数，先递归计算内层函数的结果
                if (funcExpression.contains("(")) {
                    Object innerResult = evaluate(funcExpression, context);
                    frame.addArg(innerResult);
                } else {
                    frame.addArg(funcExpression);
                }
                
                Object result = frame.execute(context);
                
                if (!expressionStack.isEmpty()) {
                    // 如果还有外层函数，将结果作为参数传入
                    expressionStack.peek().addArg(result);
                } else {
                    // 检查是否还有后续运算
                    int endIndex = evaluatedExpression.indexOf(funcExpression) + funcExpression.length();
                    String remaining = evaluatedExpression.substring(endIndex).trim();
                    
                    if (!remaining.isEmpty()) {
                        // 如果有后续运算，使用 AviatorFunction 处理
                        Function aviator = new AviatorFunction();
                        String finalExpression = result.toString() + remaining;
                        log.debug("处理剩余表达式: {}", finalExpression);
                        return aviator.execute(finalExpression, context.getProtocolContext());
                    }
                    return result;
                }
                currentExpression = new StringBuilder();
            }
        } else if (token.equals(",")) {
            if (parenthesesCount > 1) {
                currentExpression.append(token).append(" ");
            } else if (!currentExpression.toString().trim().isEmpty()) {
                expressionStack.peek().addArg(currentExpression.toString().trim());
                currentExpression = new StringBuilder();
            }
        } else {
            currentExpression.append(token).append(" ");
        }
    }
    
    // 如果没有函数调用，使用 AviatorFunction 处理
    if (expressionStack.isEmpty()) {
        Function aviator = new AviatorFunction();
        return aviator.execute(evaluatedExpression, context.getProtocolContext());
    }
    
    throw new IllegalArgumentException("表达式解析错误");
  }



  /**
   * 解析值的辅助方法
   * @param token 标记字符串
   * @return 解析后的值
   */
  private static Object parseValue(String token) {
    try {
      // 尝试解析为数字
      if (token.contains(".")) {
        return Double.parseDouble(token);
      }
      return Long.parseLong(token);
    } catch (NumberFormatException e) {
      // 如果不是数字，则返回字符串本身
      return token;
    }
  }

  private static class ExpressionFrame {

    final Function     function;
    final List<Object> args;

    ExpressionFrame(Function function) {
      this.function = function;
      this.args     = new ArrayList<>();
    }

    void addArg(Object arg) {
      args.add(arg);
    }

    Object execute(FunctionContext context) {
      StringBuilder expression = new StringBuilder(function.getName());
      expression.append("(");
      for (int i = 0; i < args.size(); i++) {
        if (i > 0) {
          expression.append(",");
        }
        expression.append(args.get(i));
      }
      expression.append(")");

      log.debug("执行函数: {}, 参数: {}", function.getName(), args);
      return function.execute(expression.toString(), context.getProtocolContext());
    }
  }
} 