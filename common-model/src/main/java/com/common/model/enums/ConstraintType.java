package com.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ConstraintType {
  TIME(1, "时间约束"),
  RESOURCE(2, "资源约束"),
  VISIBILITY(3, "可见性约束"),
  EQUIPMENT(4, "设备约束"),
  PRIORITY(5, "优先级约束"),
  ORBITAL(6, "轨道动力学约束"),
  DEPENDENCY(7, "任务依赖约束");

  @EnumValue
  private       Integer code;
  @JsonValue
  private final String  value;

  ConstraintType(Integer code, String value) {
    this.value = value;
    this.code  = code;
  }

}
