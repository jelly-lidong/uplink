package com.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 设备状态枚举
 */
@Getter
public enum EquipmentStatus {
    ACTIVE(1, "活跃"),
    INACTIVE(2, "不活跃"),
    MAINTENANCE(3, "维护中"),
    FAULT(4, "故障"),
    RESERVED(5, "已预留");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String value;

    EquipmentStatus(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
} 