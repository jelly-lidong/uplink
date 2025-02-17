package com.satellite.protocol.core.function.impl;

import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.context.ProtocolContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Crc16Function implements Function {
    // CRC-16/MODBUS 多项式
    private static final int POLYNOMIAL = 0xA001;
    private static final int INITIAL_VALUE = 0xFFFF;
    
    @Override
    public String getName() {
        return "crc16";
    }

    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        // 解析参数 - 获取需要计算CRC的组件路径
        String path = expression.substring(6, expression.length() - 1).trim();
        if (path.isEmpty()) {
            throw new IllegalArgumentException("crc16函数需要一个参数");
        }
        
        // 获取组件数据
        byte[] data = context.getComponentBytes(path);
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("未找到组件数据: " + path);
        }
        
        // 计算CRC16
        int crc = calculateCrc16(data);
        log.debug("计算CRC16: {} -> 0x{}", path, Integer.toHexString(crc));
        
        return crc;
    }
    
    /**
     * 计算CRC16 (MODBUS)
     * @param data 需要计算的数据
     * @return CRC16值
     */
    private int calculateCrc16(byte[] data) {
        int crc = INITIAL_VALUE;
        
        for (byte b : data) {
            crc ^= (b & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ POLYNOMIAL;
                } else {
                    crc = crc >>> 1;
                }
            }
        }
        
        return crc;
    }
} 