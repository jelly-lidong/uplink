package com.example.resource.service.impl;

import com.common.model.dto.TimeWindowDTO;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.common.model.enums.TaskStatus;
import com.example.resource.mapper.GroundEquipmentMapper;
import com.example.resource.mapper.TaskInfoMapper;
import com.example.resource.mapper.TaskScheduleMapper;
import com.example.resource.service.IdleResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 空闲资源服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdleResourceServiceImpl implements IdleResourceService {
    
    private final GroundEquipmentMapper groundEquipmentMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final TaskScheduleMapper taskScheduleMapper;
    
    @Override
    public List<GroundEquipment> findIdleResources(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime) {
        List<GroundEquipment> result = new ArrayList<>();
        
        try {
            // 1. 获取所有支持该任务类型的设备
            List<GroundEquipment> supportedEquipments = groundEquipmentMapper.selectByTaskType(taskInfo.getTaskType());
            if (supportedEquipments.isEmpty()) {
                log.info("没有找到支持任务类型[{}]的设备", taskInfo.getTaskType());
                return result;
            }
            
            // 2. 过滤出在指定时间段内空闲的设备
            result = supportedEquipments.stream()
                .filter(equipment -> isEquipmentIdle(equipment.getId(), startTime, endTime))
                .collect(Collectors.toList());
            
            // 3. 按设备功能单一性排序
            result = sortBySimplicity(result);
            
        } catch (Exception e) {
            log.error("查找空闲资源出错: ", e);
        }
        
        return result;
    }
    
    @Override
    public int evaluateEquipmentSimplicity(GroundEquipment equipment) {
        int score = 100; // 初始分数
        
        // 1. 检查支持的任务类型数量
        if (equipment.getSupportedTaskTypes() != null) {
            int typeCount = equipment.getSupportedTaskTypes().size();
            if (typeCount > 1) {
                score -= (typeCount - 1) * 20; // 每多支持一个任务类型扣20分
            }
        }
        
        // 2. 检查是否支持影随模式
        if (Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
            score -= 30; // 支持影随模式扣30分
        }
        
        // 3. 检查最大并行任务数
        if (equipment.getMaxParallelTasks() != null && equipment.getMaxParallelTasks() > 1) {
            score -= (equipment.getMaxParallelTasks() - 1) * 10; // 每多支持一个并行任务扣10分
        }
        
        // 确保分数在0-100之间
        return Math.max(0, Math.min(100, score));
    }
    
    @Override
    public List<GroundEquipment> sortBySimplicity(List<GroundEquipment> equipments) {
        return equipments.stream()
            .sorted(Comparator.comparing(this::evaluateEquipmentSimplicity).reversed())
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isEquipmentIdle(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 获取设备在指定时间段的调度计划
        List<TaskSchedule> schedules = taskScheduleMapper.selectByEquipmentAndTimeRange(
            equipmentId, startTime, endTime);
        
        // 2. 如果没有调度计划，说明设备空闲
        if (schedules.isEmpty()) {
            return true;
        }
        
        // 3. 检查是否有重叠的调度计划
        for (TaskSchedule schedule : schedules) {
            if (isTimeOverlap(schedule.getStartTime(), schedule.getEndTime(), startTime, endTime)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public List<TimeWindowDTO> getEquipmentIdleWindows(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        List<TimeWindowDTO> windows = new ArrayList<>();
        
        try {
            // 1. 获取设备在指定时间段的调度计划
            List<TaskSchedule> schedules = taskScheduleMapper.selectByEquipmentAndTimeRange(
                equipmentId, startTime, endTime);
            
            // 2. 如果没有调度计划，整个时间段都是空闲的
            if (schedules.isEmpty()) {
                TimeWindowDTO window = new TimeWindowDTO();
                window.setStartTime(startTime);
                window.setEndTime(endTime);
                window.setDurationMinutes(Duration.between(startTime, endTime).toMinutes());
                window.setAvailabilityRate(100.0);
                windows.add(window);
                return windows;
            }
            
            // 3. 对调度计划按开始时间排序
            schedules.sort(Comparator.comparing(TaskSchedule::getStartTime));
            
            // 4. 计算空闲时间窗口
            LocalDateTime currentTime = startTime;
            
            for (TaskSchedule schedule : schedules) {
                // 如果当前时间在调度开始时间之前，说明有空闲窗口
                if (currentTime.isBefore(schedule.getStartTime())) {
                    TimeWindowDTO window = new TimeWindowDTO();
                    window.setStartTime(currentTime);
                    window.setEndTime(schedule.getStartTime());
                    window.setDurationMinutes(Duration.between(currentTime, schedule.getStartTime()).toMinutes());
                    window.setAvailabilityRate(100.0);
                    windows.add(window);
                }
                
                // 更新当前时间为调度结束时间
                currentTime = schedule.getEndTime();
            }
            
            // 5. 检查最后一个调度计划之后是否还有空闲时间
            if (currentTime.isBefore(endTime)) {
                TimeWindowDTO window = new TimeWindowDTO();
                window.setStartTime(currentTime);
                window.setEndTime(endTime);
                window.setDurationMinutes(Duration.between(currentTime, endTime).toMinutes());
                window.setAvailabilityRate(100.0);
                windows.add(window);
            }
            
        } catch (Exception e) {
            log.error("获取设备空闲时间窗口出错: ", e);
        }
        
        return windows;
    }
    
    /**
     * 检查两个时间段是否重叠
     */
    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, 
                                LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    @Override
    public GroundEquipment findIdleResource(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 获取所有支持该任务类型的设备
        List<GroundEquipment> supportedEquipments = groundEquipmentMapper.selectByTaskType(task.getTaskType());
        
        // 2. 过滤出空闲的设备
        return supportedEquipments.stream()
                .filter(equipment -> isEquipmentAvailable(equipment.getId(), startTime, endTime))
                .filter(equipment -> isEquipmentIdle(equipment))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskSchedule allocateIdleResource(TaskInfo task, GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 检查设备是否仍然可用
        if (!isEquipmentAvailable(equipment.getId(), startTime, endTime)) {
            return null;
        }
        
        // 2. 检查设备是否仍然空闲
        if (!isEquipmentIdle(equipment)) {
            return null;
        }
        
        // 3. 创建新的任务调度
        TaskSchedule schedule = new TaskSchedule();
        schedule.setTaskId(task.getId());
        schedule.setGroundStationId(equipment.getGroundStationId());
        schedule.setEquipmentId(equipment.getId());
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setStatus(TaskStatus.PROCESSING);
        
        // 4. 保存任务调度
        taskScheduleMapper.insert(schedule);
        
        return schedule;
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
    
    /**
     * 检查设备是否空闲
     */
    private boolean isEquipmentIdle(GroundEquipment equipment) {
        // 检查设备当前状态
        return "IDLE".equals(equipment.getStatus());
    }

    @Override
    public GroundEquipment findAvailableIdleResource(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 查找所有空闲资源
            List<GroundEquipment> idleResources = findIdleResources(task, startTime, endTime);
            
            // 如果有可用的空闲资源，返回第一个
            if (!idleResources.isEmpty()) {
                return idleResources.get(0);
            }
            
            return null;
        } catch (Exception e) {
            log.error("查找可用空闲资源失败", e);
            return null;
        }
    }
}