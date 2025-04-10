package com.example.resource.service.strategy;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;

import java.time.LocalDateTime;

/**
 * 资源申请策略接口
 */
public interface ResourceApplicationStrategy {
    
    /**
     * 尝试申请资源
     * 
     * @param taskInfo 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 资源申请结果
     */
    ResourceApplicationResult apply(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取策略名称
     * 
     * @return 策略名称
     */
    String getStrategyName();
} 