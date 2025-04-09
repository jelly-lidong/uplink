package com.common.model.entity.task;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.common.model.enums.ShadowMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 影随关系实体类
 */
@Data
@TableName("shadow_relation")
@Schema(description = "影随关系")
public class ShadowRelation {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "主任务调度ID")
    private Long primaryScheduleId;
    
    @Schema(description = "影随任务调度ID")
    private Long shadowScheduleId;
    
    @Schema(description = "影随模式")
    private ShadowMode shadowMode;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "资源占用率(%)")
    private Double resourceOccupancy;
    
    @Schema(description = "是否可拆分")
    private Boolean splittable;
    
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 