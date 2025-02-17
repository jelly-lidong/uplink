package com.satellite.protocol.core.function.impl;

import com.satellite.protocol.core.function.Function;
import com.satellite.protocol.core.context.ProtocolContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecondsFunction implements Function {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public String getName() {
        return "seconds";
    }
    
    @Override
    public Object execute(String expression, ProtocolContext context) {
        // 解析参数 seconds('2020-01-02 00:00:00' - '2020-01-01 00:00:00')
        String params = expression.substring(8, expression.length() - 1);
        String[] dates = params.split("\\s*-\\s*");
        
        if (dates.length != 2) {
            throw new IllegalArgumentException("秒差值表达式格式错误: " + expression);
        }
        
        // 去除日期字符串的引号
        String date1 = dates[0].replaceAll("'", "").trim();
        String date2 = dates[1].replaceAll("'", "").trim();
        
        try {
            LocalDateTime time1 = LocalDateTime.parse(date1, FORMATTER);
            LocalDateTime time2 = LocalDateTime.parse(date2, FORMATTER);
            
            // 计算秒数差
            long seconds = ChronoUnit.SECONDS.between(time2, time1);
            log.debug("计算秒数差: {} - {} = {} s", date1, date2, seconds);
            
            return seconds;
        } catch (Exception e) {
            throw new IllegalArgumentException("日期格式错误: " + e.getMessage());
        }
    }
} 