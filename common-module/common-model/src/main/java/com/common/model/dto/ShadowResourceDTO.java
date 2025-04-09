package com.common.model.dto;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.resource.GroundStation;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.common.model.enums.ShadowMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 影随资源数据传输对象
 */
@Data
@Schema(description = "影随资源数据传输对象")
public class ShadowResourceDTO {
    
    @Schema(description = "影随模式")
    private ShadowMode shadowMode;
    
    @Schema(description = "主任务信息")
    private TaskInfo primaryTask;
    
    @Schema(description = "影随任务信息")
    private TaskInfo shadowTask;
    
    @Schema(description = "主任务调度")
    private TaskSchedule primarySchedule;
    
    @Schema(description = "影随任务调度")
    private TaskSchedule shadowSchedule;
    
    @Schema(description = "地面站信息")
    private GroundStation groundStation;
    
    @Schema(description = "设备信息")
    private GroundEquipment equipment;
    
    @Schema(description = "时间重叠开始")
    private LocalDateTime overlapStart;
    
    @Schema(description = "时间重叠结束")
    private LocalDateTime overlapEnd;
    
    @Schema(description = "重叠时长(分钟)")
    private Long overlapDuration;
    
    @Schema(description = "资源占用率(%)")
    private Double resourceOccupancy;
    
    @Schema(description = "可用时间窗口")
    private List<TimeWindowDTO> availableTimeWindows;
    
    @Schema(description = "匹配度评分(0-100)")
    private Double matchScore;
} 