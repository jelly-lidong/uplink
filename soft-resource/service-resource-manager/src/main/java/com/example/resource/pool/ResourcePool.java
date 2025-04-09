package com.example.resource.pool;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源池接口
 */
public interface ResourcePool {
    /**
     * 获取资源池类型
     */
    String getPoolType();

    /**
     * 获取优先级
     */
    int getPriority();

    /**
     * 是否支持硬实时调度
     */
    boolean isRealTime();

    /**
     * 获取可用资源
     */
    List<GroundEquipment> getAvailableResources(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime);
} 