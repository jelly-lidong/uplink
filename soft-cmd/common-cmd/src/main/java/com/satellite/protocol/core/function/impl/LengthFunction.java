package com.satellite.protocol.core.function.impl;

import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.context.ProtocolContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LengthFunction implements Function {
    @Override
    public String getName() {
        return "length";
    }

    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        // 移除.length后缀
        String path = expression.substring(0, expression.length() - 7);
        return context.getComponentLength(path);
    }
} 