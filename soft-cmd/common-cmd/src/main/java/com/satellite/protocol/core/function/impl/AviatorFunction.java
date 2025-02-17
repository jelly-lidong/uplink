package com.satellite.protocol.core.function.impl;

import com.googlecode.aviator.AviatorEvaluator;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.core.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AviatorFunction implements Function {
    
    static {
        // 注册所有自定义函数
        AviatorEvaluator.addFunction(new DayFunction());
        AviatorEvaluator.addFunction(new SecondsFunction());
        AviatorEvaluator.addFunction(new MilliSecondsFunction());
        AviatorEvaluator.addFunction(new Crc16Function());
        AviatorEvaluator.addFunction(new Crc32Function());
        AviatorEvaluator.addFunction(new LengthFunction());
        // ... 注册其他函数
    }
    
    @Override
    public String getName() {
        return "aviator";
    }
    
    @Override
    public boolean matches(String expression) {
        return false;
    }
    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        // 执行表达式
        return AviatorEvaluator.execute(expression);
    }
}
