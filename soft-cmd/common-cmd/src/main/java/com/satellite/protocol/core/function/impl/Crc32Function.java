package com.satellite.protocol.core.function.impl;

import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.context.ProtocolContext;
import lombok.extern.slf4j.Slf4j;
import java.util.zip.CRC32;

@Slf4j
public class Crc32Function implements Function {
    
    @Override
    public String getName() {
        return "crc32";
    }

    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        // 解析参数 - 获取需要计算CRC的组件路径
        String path = expression.substring(6, expression.length() - 1).trim();
        if (path.isEmpty()) {
            throw new IllegalArgumentException("crc32函数需要一个参数");
        }
        
        // 获取组件数据
        byte[] data = context.getComponentBytes(path);
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("未找到组件数据: " + path);
        }
        
        // 计算CRC32
        long crc = calculateCrc32(data);
        log.debug("计算CRC32: {} -> 0x{}", path, Long.toHexString(crc));
        
        return crc;
    }
    
    /**
     * 计算CRC32
     * 使用Java内置的CRC32实现
     * @param data 需要计算的数据
     * @return CRC32值
     */
    private long calculateCrc32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }
} 