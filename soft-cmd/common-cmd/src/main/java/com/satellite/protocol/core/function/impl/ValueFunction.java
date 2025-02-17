package com.satellite.protocol.core.function.impl;

import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.context.ProtocolContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValueFunction implements Function {
    @Override
    public String getName() {
        return "value";
    }
    
    @Override
    public boolean matches(String expression) {
        return expression.endsWith(".value");
    }
    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        // 移除.value后缀
        String path = expression.substring(0, expression.length() - 6);
        return context.getNodeValue(path);
    }
} 