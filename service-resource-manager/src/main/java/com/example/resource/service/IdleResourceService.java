package com.example.resource.service;

import com.common.model.dto.TimeWindowDTO;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 空闲资源服务接口
 */
public interface IdleResourceService {
    
    /**
     * 查找空闲的资源
     *
     * @param task 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 空闲的资源，如果没有则返回null
     */
    GroundEquipment findIdleResource(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找可用的空闲资源（用于资源申请策略）
     *
     * @param task 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可用的空闲资源，如果没有则返回null
     */
    GroundEquipment findAvailableIdleResource(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 分配空闲资源
     *
     * @param task 任务信息
     * @param equipment 要分配的设备
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务调度信息，如果分配失败则返回null
     */
    TaskSchedule allocateIdleResource(TaskInfo task, GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找指定时间范围内的空闲资源
     *
     * @param taskInfo 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 空闲资源列表，按设备功能单一性排序
     */
    List<GroundEquipment> findIdleResources(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 评估设备功能单一性
     * 评分规则：
     * 1. 只支持单一任务类型的设备得分最高
     * 2. 支持任务类型越少，得分越高
     * 3. 支持影随模式的设备得分较低
     * 4. 最大并行任务数越少，得分越高
     *
     * @param equipment 设备信息
     * @return 功能单一性评分（0-100，越高表示功能越单一）
     */
    int evaluateEquipmentSimplicity(GroundEquipment equipment);
    
    /**
     * 根据设备功能单一性对资源列表进行排序
     *
     * @param equipments 设备列表
     * @return 排序后的设备列表（功能越单一越靠前）
     */
    List<GroundEquipment> sortBySimplicity(List<GroundEquipment> equipments);
    
    /**
     * 检查设备在指定时间段是否空闲
     *
     * @param equipmentId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否空闲
     */
    boolean isEquipmentIdle(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取设备在指定时间段的空闲时间窗口
     *
     * @param equipmentId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 空闲时间窗口列表
     */
    List<TimeWindowDTO> getEquipmentIdleWindows(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime);
} 