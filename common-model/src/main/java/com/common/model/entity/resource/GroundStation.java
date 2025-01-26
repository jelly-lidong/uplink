package com.common.model.entity.resource;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@TableName("t_ground_station")
@Schema(description = "地面站信息")
public class GroundStation {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "地面站ID")
    private Long id;
    
    @Schema(description = "地面站名称", required = true)
    private String stationName;
    
    @Schema(description = "地面站编号", example = "GS-001")
    private String stationCode;
    
    @Schema(description = "地面站类型", example = "地面站")
    private String stationType;
    
    @Schema(description = "地理位置", example = "北京市海淀区")
    private String location;
    
    @Schema(description = "纬度", example = "39.9042")
    private Double latitude;
    
    @Schema(description = "经度", example = "116.4074")
    private Double longitude;
    
    @Schema(description = "海拔高度(米)", example = "43.5")
    private Double altitude;
    
    @Schema(description = "站点状态", example = "正常运行")
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