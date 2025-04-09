package com.example.resource.service;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 可转让资源服务接口
 */
public interface TransferResourceService {
    
    /**
     * 查找可转让的资源
     *
     * @param task 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可转让的资源，如果没有则返回null
     */
    GroundEquipment findTransferableResource(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 转让资源
     *
     * @param task 任务信息
     * @param equipment 要转让的设备
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务调度信息，如果转让失败则返回null
     */
    TaskSchedule transferResource(TaskInfo task, GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找补偿资源
     * 当资源被抢占时，为被抢占的任务寻找替代资源
     *
     * @param originalEquipmentId 原设备ID
     * @param taskId 被抢占的任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可用的补偿资源列表
     */
    List<GroundEquipment> findCompensatoryResources(Long originalEquipmentId, Long taskId, 
                                                  LocalDateTime startTime, LocalDateTime endTime);
} 