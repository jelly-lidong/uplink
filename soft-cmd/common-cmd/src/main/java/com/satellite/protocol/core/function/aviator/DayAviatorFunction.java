package com.satellite.protocol.core.function.aviator;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jay
 * 日期函数
 */
@Slf4j
public class DayAviatorFunction extends AbstractFunction {

    @Override
    public String getName() {
        return "day";
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        String dateStr1 = arg1.getValue(env).toString();
        String dateStr2 = arg2.getValue(env).toString();

        LocalDate date1 = LocalDate.parse(dateStr1, FORMATTER);
        LocalDate date2 = LocalDate.parse(dateStr2, FORMATTER);

        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        return new AviatorDouble(daysBetween);
    }
}