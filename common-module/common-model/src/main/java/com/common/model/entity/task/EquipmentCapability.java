package com.common.model.entity.task;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.common.model.enums.ShadowMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备能力实体类
 */
@Data
@TableName(value = "equipment_capability", autoResultMap = true)
@Schema(description = "设备能力")
public class EquipmentCapability {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "设备ID")
    private Long equipmentId;
    
    @Schema(description = "设备名称")
    private String equipmentName;
    
    @Schema(description = "影随模式")
    private ShadowMode shadowMode;
    
    @Schema(description = "最大同时任务数")
    private Integer maxConcurrentTasks;
    
    @Schema(description = "当前任务数")
    private Integer currentTaskCount;
    
    @Schema(description = "支持的任务类型")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> supportedTaskTypes;
    
    @Schema(description = "资源负载阈值(%)")
    private Double loadThreshold;
    
    @Schema(description = "当前负载(%)")
    private Double currentLoad;
    
    @Schema(description = "状态：ACTIVE-活跃, INACTIVE-不活跃, MAINTENANCE-维护中")
    private String status;
    
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 