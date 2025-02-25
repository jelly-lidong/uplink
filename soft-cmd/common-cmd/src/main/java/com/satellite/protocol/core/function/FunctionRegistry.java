package com.satellite.protocol.core.function;

import com.googlecode.aviator.AviatorEvaluator;
import com.satellite.protocol.core.function.impl.ChecksumFunction;
import com.satellite.protocol.core.function.impl.Crc16Function;
import com.satellite.protocol.core.function.impl.Crc32Function;
import com.satellite.protocol.core.function.impl.DayFunction;
import com.satellite.protocol.core.function.impl.MilliSecondsFunction;
import com.satellite.protocol.core.function.impl.SecondsFunction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FunctionRegistry {

    static {
        AviatorEvaluator.addFunction(new ChecksumFunction());
        AviatorEvaluator.addFunction(new Crc16Function());
        AviatorEvaluator.addFunction(new Crc32Function());
        AviatorEvaluator.addFunction(new DayFunction());
        AviatorEvaluator.addFunction(new MilliSecondsFunction());
        AviatorEvaluator.addFunction(new SecondsFunction());
    }

    /**
     * 评估表达式并返回结果
     */
    public static Object evaluate(String evaluatedExpression, FunctionContext context) {
        log.debug("开始计算表达式: {}", evaluatedExpression);
        return AviatorEvaluator.execute(evaluatedExpression);
    }

} 