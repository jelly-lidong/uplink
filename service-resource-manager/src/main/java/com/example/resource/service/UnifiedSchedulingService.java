package com.example.resource.service;

import com.common.model.entity.task.TaskInfo;
import com.example.resource.model.TaskFeature;
import com.example.resource.service.strategy.ResourceApplicationResult;
import com.example.resource.strategy.SchedulingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 统一调度服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UnifiedSchedulingService {
    
    private final List<SchedulingStrategy> strategies;
    
    /**
     * 执行资源调度
     */
    public ResourceApplicationResult schedule(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("开始调度任务，任务ID: {}, 开始时间: {}, 结束时间: {}", 
                task.getId(), startTime, endTime);
        
        // 1. 提取任务特征
        TaskFeature feature = TaskFeature.fromTaskInfo(task);
        log.info("任务特征: {}", feature);
        
        // 2. 按优先级排序策略
        strategies.sort(Comparator.comparingInt(SchedulingStrategy::getPriority));
        
        // 3. 依次尝试不同的调度策略
        for (SchedulingStrategy strategy : strategies) {
            if (!strategy.supports(task)) {
                continue;
            }
            
            log.info("尝试使用{}策略", strategy.getClass().getSimpleName());
            ResourceApplicationResult result = strategy.schedule(task, startTime, endTime);
            
            if (result.isSuccess()) {
                log.info("调度成功，使用策略: {}", strategy.getClass().getSimpleName());
                return result;
            }
            
            log.warn("调度失败，策略: {}, 原因: {}", 
                    strategy.getClass().getSimpleName(), result.getMessage());
        }
        
        // 4. 所有策略都失败
        log.error("所有调度策略都失败");
        return ResourceApplicationResult.builder()
                .success(false)
                .applicationType("NONE")
                .message("所有资源申请方式都失败，无法分配资源")
                .build();
    }
}