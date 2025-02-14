package com.satellite.protocol.core.function;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FunctionRegistry {
    private static final Map<String, Function> functions = new ConcurrentHashMap<>();
    
    public static void register(Function function) {
        functions.put(function.getName(), function);
    }
    
    public static Function getFunction(String name) {
        return functions.get(name);
    }
    
    static {
        // 注册内置函数
        register(new DayFunction());           // 计算天数差
        register(new MilliSecondsFunction());  // 计算毫秒差
        register(new SecondsFunction());       // 计算秒数差
        register(new Crc16Function());         // CRC16校验
        register(new Crc32Function());         // CRC32校验
    }
} 