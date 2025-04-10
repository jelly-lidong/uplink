package com.example.resource.model;

import com.common.model.entity.task.TaskInfo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 任务特征类
 */
@Data
@Builder
public class TaskFeature {
    
    /**
     * 任务类型
     */
    private String taskType;
    
    /**
     * 优先级等级
     * 1: 低优先级
     * 2: 中优先级
     * 3: 高优先级
     */
    private Integer priorityLevel;
    
    /**
     * 是否为紧急任务
     */
    private boolean emergency;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 所需能力
     */
    private Set<String> requiredCapabilities;
    
    /**
     * 从TaskInfo创建TaskFeature
     */
    public static TaskFeature fromTaskInfo(TaskInfo taskInfo) {
        return TaskFeature.builder()
                .taskType(taskInfo.getTaskType())
                .priorityLevel(taskInfo.getPriorityLevel())
                .emergency("EMERGENCY".equals(taskInfo.getTaskType()))
                .deadline(taskInfo.getEndTime())
                .requiredCapabilities(new HashSet<>())
                .build();
    }
} 