package com.satellite.protocol.core.function.impl;

import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.context.ProtocolContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MilliSecondsFunction implements Function {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public String getName() {
        return "milliSeconds";
    }

    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        // 解析参数 millis('2020-01-02 00:00:00.000' - '2020-01-01 00:00:00.000')
        String params = expression.substring(7, expression.length() - 1);
        String[] dates = params.split("\\s*-\\s*");
        
        if (dates.length != 2) {
            throw new IllegalArgumentException("毫秒差值表达式格式错误: " + expression);
        }
        
        // 去除日期字符串的引号
        String date1 = dates[0].replaceAll("'", "").trim();
        String date2 = dates[1].replaceAll("'", "").trim();
        
        try {
            LocalDateTime time1 = LocalDateTime.parse(date1, FORMATTER);
            LocalDateTime time2 = LocalDateTime.parse(date2, FORMATTER);
            
            // 计算毫秒差
            long millis1 = time1.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long millis2 = time2.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long diff = millis1 - millis2;
            
            log.debug("计算毫秒差: {} - {} = {} ms", date1, date2, diff);
            
            return diff;
        } catch (Exception e) {
            throw new IllegalArgumentException("日期格式错误: " + e.getMessage());
        }
    }
} 