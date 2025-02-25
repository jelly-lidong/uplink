package com.satellite.protocol.core.function.impl;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ChecksumFunction extends AbstractVariadicFunction {

    @Override
    public String getName() {
        return "checksum";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> map, AviatorObject... aviatorObjects) {
        // 解析参数
        String hex = FunctionUtils.getStringValue(aviatorObjects[0], map);
        byte[] bytes = HexBin.decode(hex);

        // 计算校验和
        int sum = 0;
        for (byte b : bytes) {
            sum += (b & 0xFF);
        }

        // 返回一个字节的校验和
        return FunctionUtils.wrapReturn((byte) (sum & 0xFF));
    }

} 