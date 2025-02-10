package com.common.model.entity.task;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("task_mode")
@Schema(description = "任务模式配置")
public class TaskMode {

  @TableId(type = IdType.AUTO)
  private Long id;

  @Schema(description = "任务模板ID")
  private Long taskTemplateId;

  @Schema(description = "模式名称")
  private String modeName;

  @Schema(description = "模式类型：SCHEDULED-定时执行，PERIODIC-周期执行，CONDITIONAL-条件触发")
  private String modeType;

  @Schema(description = "执行时间，格式：HH:mm")
  private String executionTime;

  @Schema(description = "执行周期(分钟)")
  private Integer period;

  @Schema(description = "触发条件")
  private String taskCondition;

  @Schema(description = "创建时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

}