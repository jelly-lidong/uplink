package com.common.model.entity.task;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@TableName(value = "task_template", autoResultMap = true)
@Schema(name = "uplink", description = "任务模板")
public class TaskTemplate {

  @TableId(type = IdType.AUTO)
  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "模板ID")
  private String templateId;

  @Schema(description = "模板名称")
  private String templateName;

  @Schema(description = "模板描述")
  private String templateDesc;

  @Schema(description = "是否启用")
  private Boolean isEnabled;

  @TableField(exist = false)
  private List<TaskMode> taskModes;

  @TableField(exist = false)
  private List<TaskTemplateConstraint> taskTemplateConstraints;

  @TableField(typeHandler = JacksonTypeHandler.class)
  @Schema(description = "任务卫星ID集合", example = "['1', '2', '3']")
  private List<String> taskSatelliteIds;

  @TableField(typeHandler = JacksonTypeHandler.class)
  @Schema(description = "目标卫星ID集合", example = "['1', '2', '3']")
  private List<String> targetSatelliteIds;

  @TableField(typeHandler = JacksonTypeHandler.class)
  @Schema(description = "地面站ID集合", example = "['1', '2', '3']")
  private List<String> groundStationIds;

  @Schema(description = "创建时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

} 