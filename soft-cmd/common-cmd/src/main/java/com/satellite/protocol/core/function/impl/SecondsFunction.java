package com.satellite.protocol.core.function.impl;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecondsFunction extends AbstractVariadicFunction {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public String getName() {
        return "seconds";
    }
    
    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {

        // 去除日期字符串的引号
        String date1 = FunctionUtils.getStringValue(args[0], env).replaceAll("'", "").trim();
        String date2 = FunctionUtils.getStringValue(args[1], env).replaceAll("'", "").trim();
        
        try {
            LocalDateTime time1 = LocalDateTime.parse(date1, FORMATTER);
            LocalDateTime time2 = LocalDateTime.parse(date2, FORMATTER);
            
            // 计算秒数差
            long seconds = ChronoUnit.SECONDS.between(time2, time1);
            log.info("计算秒数差: {} - {} = {} s", date1, date2, seconds);
            
            return FunctionUtils.wrapReturn(seconds);
        } catch (Exception e) {
            throw new IllegalArgumentException("日期格式错误: " + e.getMessage());
        }
    }
} 