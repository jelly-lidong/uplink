package com.example.resource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.EquipmentCapability;
import com.common.model.entity.task.TaskInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface GroundEquipmentService extends IService<GroundEquipment> {
    
    GroundEquipment create(GroundEquipment equipment);
    
    GroundEquipment update(GroundEquipment equipment);
    
    void delete(Long id);
    
    List<GroundEquipment> query(String keyword, Long groundStationId, String equipmentType, String status);
    
    List<GroundEquipment> getByGroundStationId(Long groundStationId);
    
    GroundEquipment updateStatus(Long id, String status);
    
    List<GroundEquipment> getByFrequencyRange(Double minFrequency, Double maxFrequency, String frequencyBand);
    
    /**
     * 查找核心资源池中可用的设备
     *
     * @param task 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可用设备列表
     */
    List<GroundEquipment> findAvailableCoreResources(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 更新设备的影随能力
     * 
     * @param equipmentId 设备ID
     * @param supportShadowMode 是否支持影随
     * @param supportedTaskTypes 支持的任务类型
     * @param maxParallelTasks 最大并行任务数
     * @return 更新后的设备信息
     */
    GroundEquipment updateShadowCapability(Long equipmentId, Boolean supportShadowMode, 
                                          List<String> supportedTaskTypes, Integer maxParallelTasks);
    
    /**
     * 获取设备影随能力详情
     * 
     * @param equipmentId 设备ID
     * @return 设备能力详情
     */
    EquipmentCapability getEquipmentCapability(Long equipmentId);
    
    /**
     * 更新设备当前任务数
     * 
     * @param equipmentId 设备ID
     * @param count 任务数量（正数增加，负数减少）
     * @return 更新后的设备信息
     */
    GroundEquipment updateTaskCount(Long equipmentId, Integer count);
} 