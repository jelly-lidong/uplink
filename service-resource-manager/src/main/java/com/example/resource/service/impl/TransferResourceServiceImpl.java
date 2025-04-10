package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.common.model.enums.TaskStatus;
import com.example.resource.mapper.GroundEquipmentMapper;
import com.example.resource.mapper.TaskInfoMapper;
import com.example.resource.mapper.TaskScheduleMapper;
import com.example.resource.service.TransferResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 可转让资源服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferResourceServiceImpl implements TransferResourceService {
    
    private final GroundEquipmentMapper groundEquipmentMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final TaskScheduleMapper taskScheduleMapper;
    
    @Override
    public GroundEquipment findTransferableResource(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 获取所有支持该任务类型的设备
        List<GroundEquipment> supportedEquipments = groundEquipmentMapper.selectByTaskType(task.getTaskType());
        
        // 2. 过滤出可转让的设备
        return supportedEquipments.stream()
                .filter(equipment -> isEquipmentAvailable(equipment.getId(), startTime, endTime))
                .filter(equipment -> evaluateTransferSuitability(equipment, task))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskSchedule transferResource(TaskInfo task, GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 检查设备是否仍然可用
        if (!isEquipmentAvailable(equipment.getId(), startTime, endTime)) {
            return null;
        }
        
        // 2. 创建新的任务调度
        TaskSchedule schedule = new TaskSchedule();
        schedule.setTaskId(task.getId());
        schedule.setGroundStationId(equipment.getGroundStationId());
        schedule.setEquipmentId(equipment.getId());
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setStatus(TaskStatus.PROCESSING);
        
        // 3. 保存任务调度
        taskScheduleMapper.insert(schedule);
        
        return schedule;
    }
    
    /**
     * 评估设备是否适合转让
     */
    private boolean evaluateTransferSuitability(GroundEquipment equipment, TaskInfo task) {
        // 1. 检查设备是否支持影子模式
        if (Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
            return true;
        }
        
        // 2. 检查设备的功能复杂度
        // 功能较为单一的设备更适合转让
        return equipment.getCapabilities().size() <= 3;
    }
    
    /**
     * 评估设备是否适合作为补偿资源
     */
    private boolean evaluateCompensationSuitability(GroundEquipment equipment, Long taskId) {
        // 1. 获取原任务的设备使用历史
        List<TaskSchedule> history = taskScheduleMapper.getByTaskId(taskId);
        
        // 2. 检查设备是否曾经被该任务使用过
        // 优先选择曾经使用过的设备
        return history.stream()
                .anyMatch(schedule -> schedule.getEquipmentId().equals(equipment.getId()));
    }
    
    /**
     * 检查设备在指定时间段是否可用
     */
    private boolean isEquipmentAvailable(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        // 检查设备是否被其他任务占用
        List<TaskSchedule> existingSchedules = taskScheduleMapper.selectByEquipmentAndTimeRange(
                equipmentId, startTime, endTime);
        
        return existingSchedules.isEmpty();
    }

    @Override
    public List<GroundEquipment> findTransferableResources(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 获取所有支持该任务类型的设备
            List<GroundEquipment> supportedEquipments = groundEquipmentMapper.selectByTaskType(taskInfo.getTaskType());
            
            // 2. 过滤出可用的设备
            List<GroundEquipment> availableEquipments = supportedEquipments.stream()
                    .filter(equipment -> isEquipmentAvailable(equipment.getId(), startTime, endTime))
                    .collect(Collectors.toList());
            
            // 3. 评估设备是否适合转让
            List<GroundEquipment> transferableEquipments = availableEquipments.stream()
                    .filter(equipment -> evaluateTransferSuitability(equipment, taskInfo))
                    .collect(Collectors.toList());
            
            // 4. 按功能复杂度排序（功能单一的优先）
            transferableEquipments.sort(Comparator.comparingInt(this::calculateEquipmentComplexity));
            
            return transferableEquipments;
        } catch (Exception e) {
            log.error("查找可转让资源失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<GroundEquipment> findCompensatoryResources(Long originalEquipmentId, Long originalTaskId, 
                                                         LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 获取原任务信息
            TaskSchedule originalSchedule = taskScheduleMapper.selectById(originalTaskId);
            if (originalSchedule == null) {
                return new ArrayList<>();
            }
            
            // 2. 获取所有支持该任务类型的设备
            TaskInfo originalTask = taskInfoMapper.selectById(originalTaskId);
            if (originalTask == null) {
                return new ArrayList<>();
            }
            List<GroundEquipment> supportedEquipments = groundEquipmentMapper.selectByTaskType(originalTask.getTaskType());
            
            // 3. 排除原设备
            List<GroundEquipment> otherEquipments = supportedEquipments.stream()
                    .filter(equipment -> !equipment.getId().equals(originalEquipmentId))
                    .collect(Collectors.toList());
            
            // 4. 过滤出可用的设备
            List<GroundEquipment> availableEquipments = otherEquipments.stream()
                    .filter(equipment -> isEquipmentAvailable(equipment.getId(), startTime, endTime))
                    .collect(Collectors.toList());
            
            // 5. 评估设备是否适合作为补偿资源
            List<GroundEquipment> compensatoryEquipments = availableEquipments.stream()
                    .filter(equipment -> evaluateCompensationSuitability(equipment, originalTaskId))
                    .collect(Collectors.toList());
            
            // 6. 按功能复杂度排序（功能单一的优先）
            compensatoryEquipments.sort(Comparator.comparingInt(this::calculateEquipmentComplexity));
            
            return compensatoryEquipments;
        } catch (Exception e) {
            log.error("查找补偿资源失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public double getEquipmentLoad(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 获取设备在指定时间段的调度计划
            List<TaskSchedule> schedules = taskScheduleMapper.selectByEquipmentAndTimeRange(
                    equipmentId, startTime, endTime);
            
            // 2. 计算总时间跨度
            long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
            
            // 3. 计算已占用时间
            long occupiedMinutes = schedules.stream()
                    .mapToLong(schedule -> ChronoUnit.MINUTES.between(
                            schedule.getStartTime(), schedule.getEndTime()))
                    .sum();
            
            // 4. 计算负载率
            return (double) occupiedMinutes / totalMinutes;
        } catch (Exception e) {
            log.error("计算设备负载失败", e);
            return 1.0; // 出错时返回最大负载
        }
    }
    
    /**
     * 计算设备功能复杂度
     * 复杂度评分标准：
     * 1. 支持的任务类型数量
     * 2. 是否支持影随模式
     * 3. 最大并行任务数
     *
     * @param equipment 设备
     * @return 复杂度评分（值越小表示功能越单一）
     */
    private int calculateEquipmentComplexity(GroundEquipment equipment) {
        int complexity = 0;
        
        // 1. 支持的任务类型数量
        complexity += equipment.getSupportedTaskTypes().size();
        
        // 2. 是否支持影随模式
        if (Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
            complexity += 2;
        }
        
        // 3. 最大并行任务数
        complexity += equipment.getMaxParallelTasks();
        
        return complexity;
    }
} 