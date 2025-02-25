package com.satellite.protocol.core.function.impl;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MilliSecondsFunction extends AbstractVariadicFunction {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public String getName() {
        return "millSeconds";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        // 解析参数
        
        // 去除日期字符串的引号
        String date1 = FunctionUtils.getStringValue(args[0], env).replaceAll("'", "").trim();
        String date2 = FunctionUtils.getStringValue(args[1], env).replaceAll("'", "").trim();
        
        try {
            LocalDateTime time1 = LocalDateTime.parse(date1, FORMATTER);
            LocalDateTime time2 = LocalDateTime.parse(date2, FORMATTER);
            
            // 计算毫秒差
            long millis1 = time1.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long millis2 = time2.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long diff = millis1 - millis2;
            
            log.debug("计算毫秒差: {} - {} = {} ms", date1, date2, diff);
            
            return FunctionUtils.wrapReturn(diff);
        } catch (Exception e) {
            throw new IllegalArgumentException("日期格式错误: " + e.getMessage());
        }
    }
} 