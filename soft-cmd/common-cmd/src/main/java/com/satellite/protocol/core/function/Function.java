package com.satellite.protocol.core.function;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.context.ProtocolContext;

public interface Function {
    /**
     * 获取函数名称
     */
    String getName();
    
    /**
     * 检查表达式是否匹配该函数
     */
    default boolean matches(String expression){
        return getName().equals(expression);
    }
    
    /**
     * 执行函数 - 字符串表达式方式
     */
    Object execute(String expression, ProtocolContext context);

} 