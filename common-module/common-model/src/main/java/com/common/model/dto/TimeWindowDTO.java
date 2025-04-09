package com.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 时间窗口数据传输对象
 */
@Data
@Schema(description = "时间窗口数据传输对象")
public class TimeWindowDTO {
    
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "时长(分钟)")
    private Long durationMinutes;
    
    @Schema(description = "资源可用率(%)")
    private Double availabilityRate;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "备注")
    private String remark;
} 