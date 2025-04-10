package com.example.resource.strategy.impl;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.service.TransferResourceService;
import com.example.resource.service.strategy.ResourceApplicationResult;
import com.example.resource.strategy.SchedulingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 可转让资源调度策略
 */
@Component
@Order(3)
@RequiredArgsConstructor
public class TransferResourceStrategy implements SchedulingStrategy {
    
    private final TransferResourceService transferResourceService;
    
    @Override
    public ResourceApplicationResult schedule(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 查找可转让的资源
        GroundEquipment equipment = transferResourceService.findTransferableResource(task, startTime, endTime);
        if (equipment == null) {
            return ResourceApplicationResult.builder()
                    .success(false)
                    .applicationType("TRANSFER")
                    .message("没有可转让的资源")
                    .build();
        }
        
        // 2. 尝试转让资源
        TaskSchedule schedule = transferResourceService.transferResource(task, equipment, startTime, endTime);
        if (schedule == null) {
            return ResourceApplicationResult.builder()
                    .success(false)
                    .applicationType("TRANSFER")
                    .message("资源转让失败")
                    .build();
        }
        
        return ResourceApplicationResult.builder()
                .success(true)
                .applicationType("TRANSFER")
                .allocatedEquipment(equipment)
                .taskSchedule(schedule)
                .message("成功转让资源")
                .build();
    }
    
    @Override
    public int getPriority() {
        return 3;
    }
    
    @Override
    public boolean supports(TaskInfo task) {
        // 可转让资源策略只支持非紧急任务
        return !"EMERGENCY".equals(task.getTaskType());
    }
} 