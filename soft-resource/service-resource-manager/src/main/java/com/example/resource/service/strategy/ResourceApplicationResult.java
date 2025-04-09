package com.example.resource.service.strategy;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源申请结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceApplicationResult {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 申请方式
     */
    private String applicationType;
    
    /**
     * 分配的设备
     */
    private GroundEquipment allocatedEquipment;
    
    /**
     * 任务调度信息
     */
    private TaskSchedule taskSchedule;
    
    /**
     * 消息
     */
    private String message;
} 