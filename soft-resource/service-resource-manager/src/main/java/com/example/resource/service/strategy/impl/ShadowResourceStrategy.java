package com.example.resource.service.strategy.impl;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.service.ShadowResourceService;
import com.example.resource.service.strategy.ResourceApplicationResult;
import com.example.resource.service.strategy.ResourceApplicationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 影随资源申请策略
 */
@Component
@RequiredArgsConstructor
public class ShadowResourceStrategy implements ResourceApplicationStrategy {
    
    private final ShadowResourceService shadowResourceService;
    
    @Override
    public ResourceApplicationResult apply(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime) {
        GroundEquipment equipment = shadowResourceService.findAvailableShadowResource(taskInfo, startTime, endTime);
        if (equipment != null) {
            TaskSchedule schedule = shadowResourceService.allocateShadowResource(taskInfo, equipment, startTime, endTime);
            return ResourceApplicationResult.builder()
                    .success(true)
                    .applicationType("SHADOW")
                    .allocatedEquipment(equipment)
                    .taskSchedule(schedule)
                    .message("成功申请影随资源")
                    .build();
        }
        return ResourceApplicationResult.builder()
                .success(false)
                .applicationType("SHADOW")
                .message("没有可用的影随资源")
                .build();
    }
    
    @Override
    public String getStrategyName() {
        return "SHADOW";
    }
} 