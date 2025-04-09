package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.common.model.entity.task.TaskScheduleStatus;
import com.example.resource.mapper.GroundEquipmentMapper;
import com.example.resource.mapper.TaskInfoMapper;
import com.example.resource.mapper.TaskScheduleMapper;
import com.example.resource.model.TaskFeature;
import com.example.resource.service.PreemptionResourceService;
import com.example.resource.service.TransferResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源抢占服务实现类
 */
@Slf4j
@Service
public class PreemptionResourceServiceImpl implements PreemptionResourceService {

    @Autowired
    private GroundEquipmentMapper groundEquipmentMapper;

    @Autowired
    private TaskScheduleMapper taskScheduleMapper;

    @Autowired
    private TransferResourceService transferResourceService;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Override
    public List<GroundEquipment> findPreemptibleResources(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 获取所有支持该任务类型的设备
            List<GroundEquipment> supportedEquipments = groundEquipmentMapper.selectByTaskType(taskInfo.getTaskType());
            
            // 2. 获取在指定时间段内有调度的设备
            List<GroundEquipment> occupiedEquipments = new ArrayList<>();
            for (GroundEquipment equipment : supportedEquipments) {
                List<TaskSchedule> schedules = taskScheduleMapper.selectByEquipmentAndTimeRange(
                        equipment.getId(), startTime, endTime);
                if (!schedules.isEmpty()) {
                    occupiedEquipments.add(equipment);
                }
            }
            
            // 3. 过滤出可被抢占的设备
            List<GroundEquipment> preemptibleEquipments = new ArrayList<>();
            for (GroundEquipment equipment : occupiedEquipments) {
                List<TaskSchedule> schedules = taskScheduleMapper.selectByEquipmentAndTimeRange(
                        equipment.getId(), startTime, endTime);
                
                // 检查是否有可被抢占的任务
                boolean hasPreemptibleTask = schedules.stream()
                        .anyMatch(schedule -> evaluatePreemptionPossibility(schedule, taskInfo));
                
                if (hasPreemptibleTask) {
                    preemptibleEquipments.add(equipment);
                }
            }
            
            return preemptibleEquipments;
        } catch (Exception e) {
            log.error("查找可抢占资源失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean evaluatePreemptionPossibility(TaskSchedule taskSchedule, TaskInfo highPriorityTask) {
        try {
            // 1. 获取任务信息
            TaskInfo currentTask = taskInfoMapper.selectById(taskSchedule.getTaskId());
            if (currentTask == null) {
                return false;
            }
            
            // 2. 比较优先级
            int currentPriority = getTaskPriority(currentTask);
            int highPriority = getTaskPriority(highPriorityTask);
            
            // 3. 高优先级任务可以抢占低优先级任务
            // 紧急任务可以抢占任何非紧急任务
            return highPriority > currentPriority || 
                   (isEmergencyTask(highPriorityTask) && !isEmergencyTask(currentTask));
        } catch (Exception e) {
            log.error("评估抢占可能性失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executePreemption(TaskInfo highPriorityTask, Long preemptedTaskId, Long equipmentId,
                                   LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 获取被抢占的任务调度
            TaskSchedule preemptedSchedule = taskScheduleMapper.selectById(preemptedTaskId);
            if (preemptedSchedule == null) {
                return false;
            }
            
            // 2. 为被抢占的任务寻找补偿资源
            List<GroundEquipment> compensatoryResources = transferResourceService.findCompensatoryResources(
                    equipmentId, preemptedTaskId, startTime, endTime);
            
            if (compensatoryResources.isEmpty()) {
                log.warn("未找到合适的补偿资源，抢占失败");
                return false;
            }
            
            // 3. 选择第一个可用的补偿资源
            GroundEquipment compensatoryEquipment = compensatoryResources.get(0);
            
            // 4. 更新被抢占任务的调度
            preemptedSchedule.setEquipmentId(compensatoryEquipment.getId());
            taskScheduleMapper.updateById(preemptedSchedule);
            
            // 5. 创建新任务的调度
            TaskSchedule newSchedule = new TaskSchedule();
            newSchedule.setTaskId(highPriorityTask.getId());
            newSchedule.setEquipmentId(equipmentId);
            newSchedule.setStartTime(startTime);
            newSchedule.setEndTime(endTime);
            newSchedule.setIsPreempted(true);
            taskScheduleMapper.insert(newSchedule);
            
            return true;
        } catch (Exception e) {
            log.error("执行资源抢占失败", e);
            throw e;
        }
    }

    @Override
    public int getTaskPriority(TaskInfo taskInfo) {
        // 优先级规则：
        // 1. 紧急任务：优先级 5
        // 2. 高优先级任务：优先级 4
        // 3. 普通任务：优先级 3
        // 4. 低优先级任务：优先级 2
        // 5. 后台任务：优先级 1
        
        if (isEmergencyTask(taskInfo)) {
            return 5;
        }
        
        switch (taskInfo.getPriorityLevel()) {
            case 3:
                return 4;
            case 2:
                return 3;
            case 1:
                return 2;
            default:
                return 3; // 默认为普通优先级
        }
    }

    @Override
    public boolean isEmergencyTask(TaskInfo taskInfo) {
        // 判断是否为紧急任务的规则：
        // 1. 任务类型为紧急类型
        return "EMERGENCY".equals(taskInfo.getTaskType());
    }

    @Override
    public GroundEquipment findPreemptibleResource(TaskFeature taskFeature, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 获取所有可抢占的设备
        List<GroundEquipment> equipments = taskInfoMapper.findPreemptibleEquipments(
                taskFeature.getTaskType(),
                taskFeature.getPriorityLevel(),
                startTime,
                endTime
        );
        
        // 2. 选择最适合的设备
        return equipments.stream()
                .filter(equipment -> isEquipmentAvailable(equipment, startTime, endTime))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskSchedule preemptResource(TaskInfo task, GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 检查设备是否仍然可用
        if (!isEquipmentAvailable(equipment, startTime, endTime)) {
            return null;
        }
        
        // 2. 创建新的任务调度
        TaskSchedule schedule = new TaskSchedule();
        schedule.setTaskId(task.getTaskId());
        schedule.setGroundStationId(equipment.getGroundStationId());
        schedule.setEquipmentId(equipment.getEquipmentId());
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setStatus(TaskScheduleStatus.SCHEDULED);
        
        // 3. 保存任务调度
        taskScheduleMapper.insert(schedule);
        
        return schedule;
    }
    
    /**
     * 检查设备在指定时间段是否可用
     */
    private boolean isEquipmentAvailable(GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime) {
        // 检查设备是否被其他任务占用
        List<TaskSchedule> existingSchedules = taskScheduleMapper.findByEquipmentAndTimeRange(
                equipment.getEquipmentId(),
                startTime,
                endTime
        );
        
        return existingSchedules.isEmpty();
    }
} 