package com.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TaskStatus {
  PENDING(1, "待处理"),
  PROCESSING(2, "处理中"),
  COMPLETED(3, "已完成"),
  CANCELLED(4, "已取消");

  @EnumValue
  private       Integer code;
  @JsonValue
  private final String  value;

  TaskStatus(Integer code, String value) {
    this.value = value;
    this.code  = code;
  }
}
