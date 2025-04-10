package com.example.resource.strategy.impl;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.service.IdleResourceService;
import com.example.resource.service.strategy.ResourceApplicationResult;
import com.example.resource.strategy.SchedulingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 空闲资源调度策略
 */
@Component
@Order(2)
@RequiredArgsConstructor
public class IdleResourceStrategy implements SchedulingStrategy {
    
    private final IdleResourceService idleResourceService;
    
    @Override
    public ResourceApplicationResult schedule(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 查找可用的空闲资源
        GroundEquipment equipment = idleResourceService.findAvailableIdleResource(task, startTime, endTime);
        if (equipment == null) {
            return ResourceApplicationResult.builder()
                    .success(false)
                    .applicationType("IDLE")
                    .message("没有可用的空闲资源")
                    .build();
        }
        
        // 2. 分配资源
        TaskSchedule schedule = idleResourceService.allocateIdleResource(task, equipment, startTime, endTime);
        
        return ResourceApplicationResult.builder()
                .success(true)
                .applicationType("IDLE")
                .allocatedEquipment(equipment)
                .taskSchedule(schedule)
                .message("成功申请空闲资源")
                .build();
    }
    
    @Override
    public int getPriority() {
        return 2;
    }
    
    @Override
    public boolean supports(TaskInfo task) {
        // 空闲资源策略支持所有任务类型
        return true;
    }
} 