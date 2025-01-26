package com.common.model.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "t_satellite", autoResultMap = true)
@Schema(description = "卫星信息")
public class Satellite {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "卫星ID")
    private Long id;
    
    @Schema(description = "卫星中文名称", required = true)
    private String satelliteNameCn;
    
    @Schema(description = "卫星英文名称", required = true)
    private String satelliteNameEn;
    
    @Schema(description = "卫星编号", example = "SAT-001")
    private String satelliteCode;
    
    @Schema(description = "国际编号(COSPAR ID)", example = "2019-037A")
    private String cosparId;
    
    @Schema(description = "NORAD编号", example = "44547")
    private String noradId;
    
    @Schema(description = "轨道类型", example = "地球同步轨道")
    private String orbitType;
    
    // 开普勒六根数
    @Schema(description = "半长轴(千米)", example = "42164.0")
    private Double semiMajorAxis;
    
    @Schema(description = "偏心率", example = "0.0004")
    private Double eccentricity;
    
    @Schema(description = "轨道倾角(度)", example = "0.0675")
    private Double inclination;
    
    @Schema(description = "升交点赤经(度)", example = "269.8563")
    private Double rightAscension;
    
    @Schema(description = "近地点幅角(度)", example = "130.5069")
    private Double argumentOfPerigee;
    
    @Schema(description = "平近点角(度)", example = "244.0862")
    private Double meanAnomaly;
    
    @Schema(description = "历元时间")
    private LocalDateTime epochTime;
    
    @Schema(description = "两行根数集(TLE第一行)", example = "1 25544U 98067A   08264.51782528 ...")
    private String tleLine1;

    @Schema(description = "两行根数集(TLE第二行)", example = "2 25544  51.6416 247.4627 0006703 146.2422 325.0282 15.50565391263537")
    private String tleLine2;
    
    @Schema(description = "发射时间")
    private LocalDateTime launchTime;
    
    @Schema(description = "卫星状态", example = "在轨运行")
    private String status;
    
    @Schema(description = "描述信息")
    private String description;
    
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "标签列表", example = "['遥感卫星', '高分辨率', 'SAR']")
    private List<String> tags;
}