package com.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ValueType {
  INTEGER(1, "整数"),
  Double(2, "浮点数"),
  BIT(4, "二进制"),
  HEX(5, "十六进制"),
  STRING(6, "字符串"),
  TIME(3, "时间"),
  BOOLEAN(7, "布尔值");

  @EnumValue
  private       Integer code;
  @JsonValue
  private final String  value;

  ValueType(Integer code, String value) {
    this.value = value;
    this.code  = code;
  }

}
