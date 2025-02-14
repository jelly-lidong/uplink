package com.satellite.protocol.core.function;

import com.satellite.protocol.core.ProtocolException;

public interface Function {
    /**
     * 执行函数
     * @param args 函数参数
     * @return 计算结果
     */
    Object execute(Object... args) throws ProtocolException;
    
    /**
     * 获取函数名
     */
    String getName();
} 