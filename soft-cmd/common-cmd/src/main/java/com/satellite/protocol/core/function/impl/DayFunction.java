package com.satellite.protocol.core.function.impl;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DayFunction extends AbstractFunction {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public String getName() {
        return "day";
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("day函数需要2个参数");
        }
        
        String date1 = FunctionUtils.getStringValue(args[0], env).replaceAll("'", "").trim();
        String date2 = FunctionUtils.getStringValue(args[1], env).replaceAll("'", "").trim();
        
        try {
            LocalDateTime time1 = LocalDateTime.parse(date1, FORMATTER);
            LocalDateTime time2 = LocalDateTime.parse(date2, FORMATTER);
            
            long days = ChronoUnit.DAYS.between(time2, time1);
            log.debug("计算天数差: {} - {} = {} 天", date1, date2, days);
            
            return FunctionUtils.wrapReturn(days);
        } catch (Exception e) {
            throw new IllegalArgumentException("日期格式错误: " + e.getMessage());
        }
    }
} 