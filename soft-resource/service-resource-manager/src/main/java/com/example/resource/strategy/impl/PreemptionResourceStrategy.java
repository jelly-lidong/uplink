package com.example.resource.strategy.impl;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.model.TaskFeature;
import com.example.resource.service.PreemptionResourceService;
import com.example.resource.service.strategy.ResourceApplicationResult;
import com.example.resource.strategy.SchedulingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 可抢占资源调度策略
 */
@Component
@Order(4)
@RequiredArgsConstructor
public class PreemptionResourceStrategy implements SchedulingStrategy {
    
    private final PreemptionResourceService preemptionResourceService;
    
    @Override
    public ResourceApplicationResult schedule(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 提取任务特征
        TaskFeature taskFeature = TaskFeature.fromTaskInfo(task);
        
        // 2. 查找可抢占的资源
        GroundEquipment equipment = preemptionResourceService.findPreemptibleResource(taskFeature, startTime, endTime);
        if (equipment == null) {
            return ResourceApplicationResult.builder()
                    .success(false)
                    .applicationType("PREEMPTION")
                    .message("没有可抢占的资源")
                    .build();
        }
        
        // 3. 尝试抢占资源
        TaskSchedule schedule = preemptionResourceService.preemptResource(task, equipment, startTime, endTime);
        if (schedule == null) {
            return ResourceApplicationResult.builder()
                    .success(false)
                    .applicationType("PREEMPTION")
                    .message("资源抢占失败")
                    .build();
        }
        
        return ResourceApplicationResult.builder()
                .success(true)
                .applicationType("PREEMPTION")
                .allocatedEquipment(equipment)
                .taskSchedule(schedule)
                .message("成功抢占资源")
                .build();
    }
    
    @Override
    public int getPriority() {
        return 4;
    }
    
    @Override
    public boolean supports(TaskInfo task) {
        // 可抢占资源策略只支持紧急任务或高优先级任务
        TaskFeature taskFeature = TaskFeature.fromTaskInfo(task);
        return taskFeature.isEmergency() || taskFeature.getPriorityLevel() == 3;
    }
} 