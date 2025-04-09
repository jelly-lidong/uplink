package com.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 影随模式枚举
 */
@Getter
public enum ShadowMode {
    NONE(0, "不支持影随"),
    MEASUREMENT_CONTROL(1, "测控"),
    DATA_TRANSMISSION(2, "数传"),
    INTEGRATED(3, "测控数传一体化");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String value;

    ShadowMode(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
} 