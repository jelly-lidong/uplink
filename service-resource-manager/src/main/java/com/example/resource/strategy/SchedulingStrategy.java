package com.example.resource.strategy;

import com.common.model.entity.task.TaskInfo;
import com.example.resource.service.strategy.ResourceApplicationResult;

import java.time.LocalDateTime;

/**
 * 调度策略接口
 */
public interface SchedulingStrategy {
    /**
     * 执行调度
     */
    ResourceApplicationResult schedule(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取优先级
     */
    int getPriority();
    
    /**
     * 是否支持该任务类型
     */
    boolean supports(TaskInfo task);
} 