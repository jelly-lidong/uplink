package com.example.resource.pool.impl;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.example.resource.pool.ResourcePool;
import com.example.resource.service.GroundEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 核心资源池实现
 */
@Component
@Primary
@RequiredArgsConstructor
public class CoreResourcePool implements ResourcePool {
    
    private final GroundEquipmentService groundEquipmentService;
    
    @Override
    public String getPoolType() {
        return "CORE";
    }
    
    @Override
    public int getPriority() {
        return 1;  // 最高优先级
    }
    
    @Override
    public boolean isRealTime() {
        return true;
    }
    
    @Override
    public List<GroundEquipment> getAvailableResources(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        // 获取核心资源池中的可用设备
        return groundEquipmentService.findAvailableCoreResources(task, startTime, endTime);
    }
} 