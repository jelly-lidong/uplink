package com.satellite.protocol.core.function;

import com.satellite.protocol.core.ProtocolException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MilliSecondsFunction implements Function {
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public Object execute(Object... args) throws ProtocolException {
        if (args.length != 2) {
            throw new ProtocolException("MilliSeconds function requires 2 arguments");
        }
        
        try {
            LocalDateTime time1 = parseDateTime(args[0]);
            LocalDateTime time2 = parseDateTime(args[1]);
            return ChronoUnit.MILLIS.between(time2, time1);
        } catch (Exception e) {
            throw new ProtocolException("Failed to calculate milliseconds", e);
        }
    }
    
    @Override
    public String getName() {
        return "millSeconds";
    }
    
    private LocalDateTime parseDateTime(Object time) {
        if (time instanceof LocalDateTime) {
            return (LocalDateTime) time;
        }
        if ("currentDate".equals(time.toString())) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(time.toString(), FORMATTER);
    }
} 