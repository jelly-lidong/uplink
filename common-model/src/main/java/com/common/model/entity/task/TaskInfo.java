package com.common.model.entity.task;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.common.model.entity.resource.GroundStation;
import com.common.model.entity.resource.Satellite;
import com.common.model.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName(value = "task_info", autoResultMap = true)
@ApiModel(value = "TaskInfo", description = "任务信息")
public class TaskInfo {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Long id;
    
    @ApiModelProperty(value = "任务ID")
    private String taskId;
    
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    
    @ApiModelProperty(value = "任务描述")
    private String taskDesc;
    
    @ApiModelProperty(value = "优先级别：1-低，2-中，3-高")
    private Integer priorityLevel;
    
    @ApiModelProperty(value = "重要程度：1-低，2-中，3-高")
    private Integer importanceLevel;

    @ApiModelProperty(value = "任务模式")
    private String taskModeId;

    @ApiModelProperty(value = "任务类型")
    private String taskType;
    
    @ApiModelProperty(value = "任务状态")
    private TaskStatus taskStatus;

    @ApiModelProperty(value = "父任务ID")
    private String parentTaskId;
    
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "任务卫星ID集合", example = "['1', '2', '3']")
    private List<String> taskSatelliteIds;

    @TableField(exist = false)
    private List<Satellite> taskSatellites;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "目标卫星ID集合", example = "['1', '2', '3']")
    private List<String> targetSatelliteIds;

    @TableField(exist = false)
    private List<Satellite> targetSatellites;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "地面站ID集合", example = "['1', '2', '3']")
    private List<String> groundStationIds;

    @TableField(exist = false)
    private List<GroundStation> groundStations;

    @TableField(exist = false)
    private List<TaskConstraint> taskConstraints;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 