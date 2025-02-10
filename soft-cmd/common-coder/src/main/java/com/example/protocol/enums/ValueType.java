package com.example.protocol.enums;

// 值类型枚举
public enum ValueType {
    UNSIGNED_INT(0, "无符号整数"),
    SIGNED_INT(1, "有符号整数"),
    FLOAT(2, "浮点数"),
    BINARY(3, "二进制"),
    OCTAL(4, "八进制"),
    HEX(5, "十六进制"),
    TIME(6, "时间");

    private final Integer code;
    private final String desc;

    ValueType(Integer code, String desc) {

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