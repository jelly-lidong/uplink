package com.example.resource.service;

import com.common.model.dto.ShadowResourceDTO;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 影随资源服务接口
 */
public interface ShadowResourceService {
    
    /**
     * 查找任务可影随的资源
     *
     * @param taskInfo 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可影随资源列表
     */
    List<ShadowResourceDTO> findShadowResources(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 检查设备是否支持影随
     *
     * @param equipment 设备信息
     * @param taskInfo 任务信息
     * @return 是否支持
     */
    boolean isShadowSupported(GroundEquipment equipment, TaskInfo taskInfo);
    
    /**
     * 申请影随资源
     *
     * @param primaryTaskId 主任务ID
     * @param shadowTaskId 影随任务ID
     * @param equipmentId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 影随资源信息
     */
    ShadowResourceDTO applyShadowResource(Long primaryTaskId, Long shadowTaskId, Long equipmentId, 
                                         LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务现有的影随关系
     *
     * @param taskId 任务ID
     * @return 影随资源列表
     */
    List<ShadowResourceDTO> getTaskShadowRelations(Long taskId);
    
    /**
     * 释放影随资源
     *
     * @param taskScheduleId 任务调度ID
     * @return 是否成功
     */
    boolean releaseShadowResource(Long taskScheduleId);
    
    /**
     * 查找主任务所有的影随任务
     *
     * @param primaryTaskId 主任务ID
     * @return 影随任务列表
     */
    List<TaskInfo> findShadowTasks(Long primaryTaskId);
    
    /**
     * 查找影随任务的主任务
     *
     * @param shadowTaskId 影随任务ID
     * @return 主任务信息
     */
    TaskInfo findPrimaryTask(Long shadowTaskId);
    
    /**
     * 获取设备当前调度计划
     *
     * @param equipmentId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调度计划列表
     */
    List<TaskSchedule> getEquipmentSchedules(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 计算时间重叠
     *
     * @param startTime1 时间1开始
     * @param endTime1 时间1结束
     * @param startTime2 时间2开始
     * @param endTime2 时间2结束
     * @return 重叠时间(分钟)，-1表示无重叠
     */
    long calculateTimeOverlap(LocalDateTime startTime1, LocalDateTime endTime1, 
                             LocalDateTime startTime2, LocalDateTime endTime2);
} 