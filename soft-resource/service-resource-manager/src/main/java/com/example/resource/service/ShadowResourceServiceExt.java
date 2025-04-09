package com.example.resource.service;

import com.common.model.dto.ShadowResourceDTO;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 影随资源服务扩展接口
 */
public interface ShadowResourceServiceExt extends ShadowResourceService {
    
    /**
     * 根据设备ID和任务类型查询可以影随的设备列表
     *
     * @param equipmentId 设备ID
     * @param taskType 任务类型
     * @return 可影随的设备列表
     */
    List<GroundEquipment> findShadowableEquipmentsByType(Long equipmentId, String taskType);
    
    /**
     * 查询设备是否支持指定的影随任务类型
     *
     * @param equipmentId 设备ID
     * @param taskType 任务类型
     * @return 是否支持
     */
    boolean isSupportedTaskType(Long equipmentId, String taskType);
    
    /**
     * 根据时间段查询可用于影随的任务
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param taskType 任务类型
     * @return 可影随的任务列表
     */
    List<TaskInfo> findShadowableTasksByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String taskType);
    
    /**
     * 根据主任务ID查询可以影随该任务的任务列表
     *
     * @param primaryTaskId 主任务ID
     * @return 可影随任务列表
     */
    List<ShadowResourceDTO> findPotentialShadowsForTask(Long primaryTaskId);
    
    /**
     * 根据设备ID查询该设备上可以作为影随目标的任务列表
     *
     * @param equipmentId 设备ID
     * @return 可作为影随目标的任务列表
     */
    List<TaskInfo> findPotentialPrimaryTasksOnEquipment(Long equipmentId);
    
    /**
     * 统计设备影随资源利用率
     *
     * @param equipmentId 设备ID
     * @return 设备影随资源利用率(%)
     */
    Double calculateEquipmentShadowUtilizationRate(Long equipmentId);
    
    /**
     * 查询所有支持影随的设备列表
     *
     * @return 支持影随的设备列表
     */
    List<GroundEquipment> findAllShadowSupportedEquipments();
} 