package com.example.resource.strategy.impl;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.pool.ResourcePool;
import com.example.resource.service.ShadowResourceService;
import com.example.resource.service.strategy.ResourceApplicationResult;
import com.example.resource.strategy.SchedulingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 影随调度策略
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class ShadowSchedulingStrategy implements SchedulingStrategy {
    
    private final ShadowResourceService shadowResourceService;
    private final ResourcePool coreResourcePool;
    
    @Override
    public ResourceApplicationResult schedule(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 查找可用的影随资源
        GroundEquipment equipment = shadowResourceService.findAvailableShadowResource(task, startTime, endTime);
        if (equipment == null) {
            return ResourceApplicationResult.builder()
                    .success(false)
                    .applicationType("SHADOW")
                    .message("没有可用的影随资源")
                    .build();
        }
        
        // 2. 分配资源
        TaskSchedule schedule = shadowResourceService.allocateShadowResource(task, equipment, startTime, endTime);
        
        return ResourceApplicationResult.builder()
                .success(true)
                .applicationType("SHADOW")
                .allocatedEquipment(equipment)
                .taskSchedule(schedule)
                .message("成功申请影随资源")
                .build();
    }
    
    @Override
    public int getPriority() {
        return 1;
    }
    
    @Override
    public boolean supports(TaskInfo task) {
        // 影随策略支持所有任务类型
        return true;
    }
} 