package com.example.resource.service;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.model.TaskFeature;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源抢占服务接口
 * 用于处理高优先级任务抢占低优先级任务资源的情况
 */
public interface PreemptionResourceService {
    
    /**
     * 查找可抢占的资源
     * 根据任务优先级查找可被抢占的低优先级任务资源
     *
     * @param taskInfo 高优先级任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可抢占的设备列表
     */
    List<GroundEquipment> findPreemptibleResources(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 评估任务是否可被抢占
     * 根据任务优先级判断
     *
     * @param taskSchedule 待评估的任务调度
     * @param highPriorityTask 高优先级任务
     * @return 是否可被抢占
     */
    boolean evaluatePreemptionPossibility(TaskSchedule taskSchedule, TaskInfo highPriorityTask);
    
    /**
     * 执行资源抢占
     * 1. 取消原任务的调度
     * 2. 为原任务寻找补偿资源
     * 3. 为新任务分配抢占的资源
     *
     * @param highPriorityTask 高优先级任务
     * @param preemptedTaskId 被抢占的任务ID
     * @param equipmentId 被抢占的设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否抢占成功
     */
    boolean executePreemption(TaskInfo highPriorityTask, Long preemptedTaskId, Long equipmentId,
                            LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务的优先级
     *
     * @param taskInfo 任务信息
     * @return 优先级值（值越大优先级越高）
     */
    int getTaskPriority(TaskInfo taskInfo);
    
    /**
     * 检查是否为紧急任务
     *
     * @param taskInfo 任务信息
     * @return 是否为紧急任务
     */
    boolean isEmergencyTask(TaskInfo taskInfo);

    /**
     * 查找可抢占的资源
     *
     * @param taskFeature 任务特征
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可抢占的资源，如果没有则返回null
     */
    GroundEquipment findPreemptibleResource(TaskFeature taskFeature, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 抢占资源
     *
     * @param task 任务信息
     * @param equipment 要抢占的设备
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务调度信息，如果抢占失败则返回null
     */
    TaskSchedule preemptResource(TaskInfo task, GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime);
} 