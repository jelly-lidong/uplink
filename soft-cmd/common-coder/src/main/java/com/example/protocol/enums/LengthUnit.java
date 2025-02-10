package com.example.protocol.enums;

/**
 * 长度单位枚举，表示节点的长度单位。
 */
public enum LengthUnit {
    BIT(0, "位"),
    BYTE(1, "字节");

    private final Integer code; // 枚举值的代码
    private final String desc; // 枚举值的描述

    LengthUnit(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
} 