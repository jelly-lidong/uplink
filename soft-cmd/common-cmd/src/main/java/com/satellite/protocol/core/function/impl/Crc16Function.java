package com.satellite.protocol.core.function.impl;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.satellite.protocol.core.context.ProtocolContext;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class Crc16Function extends AbstractVariadicFunction {
    // CRC-16/MODBUS 多项式
    private static final int POLYNOMIAL = 0xA001;
    private static final int INITIAL_VALUE = 0xFFFF;
    
    @Override
    public String getName() {
        return "crc16";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        // 解析参数
        String hex = FunctionUtils.getStringValue(args[0], env);
        
        // 获取组件数据
        byte[] data = HexBin.decode(hex);
        
        // 计算CRC16
        int crc = calculateCrc16(data);
        return FunctionUtils.wrapReturn(crc);
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