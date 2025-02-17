package com.satellite.protocol.core.function.impl;

import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.context.ProtocolContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChecksumFunction implements Function {
    @Override
    public String getName() {
        return "checksum";
    }
    
    @Override
    public boolean matches(String expression) {
        return expression.startsWith("checksum(") && expression.endsWith(")");
    }
    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        // 解析参数
        String path = expression.substring(9, expression.length() - 1);
        
        // 获取数据
        byte[] data = context.getComponentBytes(path);
        
        // 计算校验和
        int sum = 0;
        for (byte b : data) {
            sum += (b & 0xFF);
        }
        
        // 返回一个字节的校验和
        return (byte)(sum & 0xFF);
    }
} 