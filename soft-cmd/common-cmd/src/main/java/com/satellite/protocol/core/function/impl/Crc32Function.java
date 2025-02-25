package com.satellite.protocol.core.function.impl;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.satellite.protocol.core.context.ProtocolContext;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import lombok.extern.slf4j.Slf4j;
import java.util.zip.CRC32;
import java.util.Map;

@Slf4j
public class Crc32Function extends AbstractVariadicFunction {
    @Override
    public String getName() {
        return "crc32";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        // 解析参数
        String hex = FunctionUtils.getStringValue(args[0], env);
        byte[] bytes = HexBin.decode(hex);

        // 计算CRC32
        long crc = calculateCrc32(bytes);

        return FunctionUtils.wrapReturn(crc);
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