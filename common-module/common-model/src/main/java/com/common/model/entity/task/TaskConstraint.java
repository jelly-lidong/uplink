package com.common.model.entity.task;

import com.baomidou.mybatisplus.annotation.*;
import com.common.model.enums.ConstraintType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_constraint")
@Schema(description = "任务约束")
public class TaskConstraint {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "任务模板ID")
    private Long taskId;

    @Schema(description = "约束类型：TIME-时间约束，RESOURCE-资源约束，VISIBILITY-可见性约束，" +
        "EQUIPMENT-设备约束，PRIORITY-优先级约束，ORBITAL-轨道动力学约束，DEPENDENCY-任务依赖约束")
    private ConstraintType constraintType;

    @Schema(description = "约束名称")
    private String name;

    @Schema(description = "约束描述")
    private String description;

    @Schema(description = "约束最小值")
    private Double minValue;

    @Schema(description = "约束最大值")
    private Double maxValue;

    @Schema(description = "约束值")
    private Double value;

    @Schema(description = "最大迭代次数")
    private int maxIter;

    @Schema(description = "最大检查次数")
    private double maxCheck;

    @Schema(description = "约束阈值")
    private double threshold;

    @Schema(description = "约束数据类型：1-数值，2-字符串，3-布尔，4-日期")
    private Integer dataType;

    @Schema(description = " 是否启用 false-禁用，true-启用")
    private Boolean isEnabled;

    @Schema(description = "是否必填：0-否，1-是")
    private Integer required;

    @Schema(description = "排序号")
    private Integer sortNo;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;



} 