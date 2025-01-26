package com.common.model.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@TableName("t_ground_equipment")
@Schema(description = "地面站装备信息")
public class GroundEquipment {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "装备ID")
    private Long id;
    
    @Schema(description = "所属地面站ID")
    private Long groundStationId;
    
    @Schema(description = "装备名称", required = true)
    private String equipmentName;
    
    @Schema(description = "装备编号", example = "EQ-001")
    private String equipmentCode;
    
    @Schema(description = "装备类型", example = "天线")
    private String equipmentType;
    
    @Schema(description = "最小工作频率(MHz)", example = "2200")
    private Double minFrequency;
    
    @Schema(description = "最大工作频率(MHz)", example = "2300")
    private Double maxFrequency;
    
    @Schema(description = "频段", example = "S")
    private String frequencyBand;
    
    @Schema(description = "极化方式", example = "RHCP/LHCP")
    private String polarization;
    
    @Schema(description = "天线增益(dB)", example = "45.5")
    private Double gain;
    
    @Schema(description = "装备状态", example = "正常使用")
    private String status;
    
    @Schema(description = "描述信息")
    private String description;
    
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}