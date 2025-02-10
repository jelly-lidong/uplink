package com.example.protocol.enums;

import lombok.Getter;

/**
 * 字节序类型枚举，表示数据的字节序。
 */
@Getter
public enum EndianType {
    BIG_BYTE(0, "大端字节序"),
    LITTLE_BYTE(1, "小端字节序"),
    BIG_BIT(2, "大端位序"),
    LITTLE_BIT(3, "小端位序");

    private final Integer code; // 枚举值的代码
    private final String desc; // 枚举值的描述

    EndianType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
