package com.example.resource.service.impl;

import com.common.model.dto.ShadowResourceDTO;
import com.common.model.dto.TimeWindowDTO;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.resource.GroundStation;
import com.common.model.entity.task.ShadowRelation;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.common.model.enums.ShadowMode;
import com.common.model.enums.TaskStatus;
import com.example.resource.mapper.GroundEquipmentMapper;
import com.example.resource.mapper.TaskInfoMapper;
import com.example.resource.mapper.TaskScheduleMapper;
import com.example.resource.service.ShadowResourceService;
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
 * 影子资源服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShadowResourceServiceImpl implements ShadowResourceService {
    
    private final GroundEquipmentMapper groundEquipmentMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final TaskScheduleMapper taskScheduleMapper;
    
    @Override
    public List<ShadowResourceDTO> findShadowResources(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime) {
        List<ShadowResourceDTO> result = new ArrayList<>();
        
        try {
            // 1. 获取当前已有任务调度
            List<TaskSchedule> existingSchedules = getExistingSchedulesInTimeRange(startTime, endTime);
            if (existingSchedules.isEmpty()) {
                log.info("在指定时间范围内没有找到已分配的计划，无法进行影随");
                return result;
            }
            
            // 2. 遍历已有调度，查找能否影随
            for (TaskSchedule schedule : existingSchedules) {
                // 2.1 获取设备信息
                GroundEquipment equipment = getEquipmentById(schedule.getEquipmentId());
                if (equipment == null || !Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
                    continue; // 设备不支持影随
                }
                
                // 2.2 检查设备是否支持新任务的类型
                if (!isShadowSupported(equipment, taskInfo)) {
                    continue;
                }
                
                // 2.3 获取主任务信息
                TaskInfo primaryTask = getTaskById(schedule.getTaskId());
                if (primaryTask == null) {
                    continue;
                }
                
                // 2.4 检查时间重叠
                long overlapMinutes = calculateTimeOverlap(
                    schedule.getStartTime(), schedule.getEndTime(), 
                    startTime, endTime
                );
                
                if (overlapMinutes <= 0) {
                    continue; // 没有时间重叠
                }
                
                // 2.5 计算资源占用率和匹配度
                Double resourceOccupancy = calculateResourceOccupancy(equipment, primaryTask, taskInfo);
                Double matchScore = calculateMatchScore(equipment, primaryTask, taskInfo, overlapMinutes);
                
                // 2.6 计算重叠时间窗口
                LocalDateTime overlapStart = schedule.getStartTime().isAfter(startTime) ? 
                    schedule.getStartTime() : startTime;
                LocalDateTime overlapEnd = schedule.getEndTime().isBefore(endTime) ? 
                    schedule.getEndTime() : endTime;
                
                // 2.7 创建影随资源DTO并添加到结果
                ShadowResourceDTO shadowResource = new ShadowResourceDTO();
                shadowResource.setShadowMode(equipment.getSupportedTaskTypes().size() > 1 ? 
                    ShadowMode.INTEGRATED : getShadowModeByTaskType(taskInfo.getTaskType()));
                shadowResource.setPrimaryTask(primaryTask);
                shadowResource.setShadowTask(taskInfo);
                shadowResource.setPrimarySchedule(schedule);
                shadowResource.setEquipment(equipment);
                shadowResource.setGroundStation(getGroundStationById(equipment.getGroundStationId()));
                shadowResource.setOverlapStart(overlapStart);
                shadowResource.setOverlapEnd(overlapEnd);
                shadowResource.setOverlapDuration(overlapMinutes);
                shadowResource.setResourceOccupancy(resourceOccupancy);
                shadowResource.setMatchScore(matchScore);
                
                // 计算可用时间窗口
                List<TimeWindowDTO> timeWindows = calculateAvailableTimeWindows(
                    schedule, startTime, endTime);
                shadowResource.setAvailableTimeWindows(timeWindows);
                
                result.add(shadowResource);
            }
            
            // 3. 按匹配度排序
            result.sort(Comparator.comparing(ShadowResourceDTO::getMatchScore).reversed());
            
        } catch (Exception e) {
            log.error("查找影随资源出错: ", e);
        }
        
        return result;
    }
    
    @Override
    public boolean isShadowSupported(GroundEquipment equipment, TaskInfo taskInfo) {
        // 检查设备是否支持影随
        if (equipment == null || !Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
            return false;
        }
        
        // 检查设备是否支持任务类型
        if (equipment.getSupportedTaskTypes() == null || 
            !equipment.getSupportedTaskTypes().contains(taskInfo.getTaskType())) {
            return false;
        }
        
        // 检查设备负载状态
        if (equipment.getCurrentParallelTasks() >= equipment.getMaxParallelTasks()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskSchedule allocateShadowResource(TaskInfo task, GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 检查设备是否仍然支持影子模式
            if (!isSupportShadowMode(equipment)) {
                log.warn("设备[{}]不支持影子模式", equipment.getId());
                return null;
            }
            
            // 2. 检查设备是否仍然可分配影子资源
            if (!isShadowResourceAvailable(equipment, startTime, endTime)) {
                log.warn("设备[{}]在指定时间段内不可分配影子资源", equipment.getId());
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
            
        } catch (Exception e) {
            log.error("分配影子资源出错: ", e);
            return null;
        }
    }
    
    @Override
    public boolean isSupportShadowMode(GroundEquipment equipment) {
        return Boolean.TRUE.equals(equipment.getSupportShadowMode());
    }
    
    @Override
    public boolean isShadowResourceAvailable(GroundEquipment equipment, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 获取设备在指定时间段的调度计划
            List<TaskSchedule> schedules = taskScheduleMapper.selectByEquipmentAndTimeRange(
                    equipment.getId(), startTime, endTime);
            
            // 2. 检查是否有重叠的调度计划
            for (TaskSchedule schedule : schedules) {
                if (isTimeOverlap(schedule.getStartTime(), schedule.getEndTime(), startTime, endTime)) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("检查影子资源可用性出错: ", e);
            return false;
        }
    }
    
    @Override
    public List<ShadowResourceDTO> getTaskShadowRelations(Long taskId) {
        // 获取任务的所有影随关系
        // 此处需实现具体逻辑
        return new ArrayList<>();
    }
    
    @Override
    @Transactional
    public boolean releaseShadowResource(Long taskScheduleId) {
        // 释放影随资源
        // 此处需实现具体逻辑
        return true;
    }
    
    @Override
    public List<TaskInfo> findShadowTasks(Long primaryTaskId) {
        // 查找主任务的所有影随任务
        // 此处需实现具体逻辑
        return new ArrayList<>();
    }
    
    @Override
    public TaskInfo findPrimaryTask(Long shadowTaskId) {
        // 查找影随任务的主任务
        // 此处需实现具体逻辑
        return null;
    }
    
    @Override
    public List<TaskSchedule> getEquipmentSchedules(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        // 获取设备在指定时间范围内的调度计划
        // 此处需实现具体逻辑
        return new ArrayList<>();
    }
    
    @Override
    public long calculateTimeOverlap(LocalDateTime startTime1, LocalDateTime endTime1, 
                                    LocalDateTime startTime2, LocalDateTime endTime2) {
        // 计算两个时间段的重叠时间（分钟）
        if (startTime1.isAfter(endTime2) || startTime2.isAfter(endTime1)) {
            return -1; // 没有重叠
        }
        
        LocalDateTime overlapStart = startTime1.isAfter(startTime2) ? startTime1 : startTime2;
        LocalDateTime overlapEnd = endTime1.isBefore(endTime2) ? endTime1 : endTime2;
        
        return Duration.between(overlapStart, overlapEnd).toMinutes();
    }
    
    /**
     * 检查两个时间段是否重叠
     */
    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, 
                                LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
    
    // ========================= 私有辅助方法 =========================
    
    /**
     * 获取指定时间范围内的现有调度计划
     */
    private List<TaskSchedule> getExistingSchedulesInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        // 此处需实现具体逻辑
        return new ArrayList<>();
    }
    
    /**
     * 根据设备ID获取设备信息
     */
    private GroundEquipment getEquipmentById(Long equipmentId) {
        // 此处需实现具体逻辑
        return null;
    }
    
    /**
     * 根据任务ID获取任务信息
     */
    private TaskInfo getTaskById(Long taskId) {
        // 此处需实现具体逻辑
        return null;
    }
    
    /**
     * 根据地面站ID获取地面站信息
     */
    private GroundStation getGroundStationById(Long stationId) {
        // 此处需实现具体逻辑
        return null;
    }
    
    /**
     * 计算资源占用率
     */
    private Double calculateResourceOccupancy(GroundEquipment equipment, TaskInfo primaryTask, TaskInfo shadowTask) {
        // 根据设备类型和任务类型计算资源占用率
        // 此处是简单示例，实际可能需要更复杂的计算
        return 0.5; // 示例：占用率50%
    }
    
    /**
     * 计算匹配度评分
     */
    private Double calculateMatchScore(GroundEquipment equipment, TaskInfo primaryTask, 
                                     TaskInfo shadowTask, long overlapMinutes) {
        // 计算匹配度评分，考虑多种因素：
        // 1. 时间重叠比例
        // 2. 设备负载状态
        // 3. 任务类型匹配度
        // 4. 优先级差异
        
        double timeScore = Math.min(100.0, overlapMinutes / 10.0); // 示例：重叠时间越长分数越高
        double loadScore = 100.0 * (1 - (double)equipment.getCurrentParallelTasks() / equipment.getMaxParallelTasks());
        double typeScore = 80.0; // 任务类型匹配度，简化处理
        double priorityScore = 100.0 - Math.abs(primaryTask.getPriorityLevel() - shadowTask.getPriorityLevel()) * 20;
        
        // 综合评分，可以根据实际情况调整权重
        return (timeScore * 0.4 + loadScore * 0.3 + typeScore * 0.2 + priorityScore * 0.1);
    }
    
    /**
     * 根据任务类型获取影随模式
     */
    private ShadowMode getShadowModeByTaskType(String taskType) {
        if (taskType == null) {
            return ShadowMode.NONE;
        }
        
        switch (taskType.toUpperCase()) {
            case "测控":
                return ShadowMode.MEASUREMENT_CONTROL;
            case "数传":
                return ShadowMode.DATA_TRANSMISSION;
            case "一体化":
                return ShadowMode.INTEGRATED;
            default:
                return ShadowMode.NONE;
        }
    }
    
    /**
     * 计算可用时间窗口
     */
    private List<TimeWindowDTO> calculateAvailableTimeWindows(TaskSchedule primarySchedule, 
                                                            LocalDateTime requestStart, 
                                                            LocalDateTime requestEnd) {
        List<TimeWindowDTO> windows = new ArrayList<>();
        
        // 计算所有可能的时间窗口
        // 1. 主任务调度开始前的窗口
        if (requestStart.isBefore(primarySchedule.getStartTime())) {
            TimeWindowDTO before = new TimeWindowDTO();
            before.setStartTime(requestStart);
            before.setEndTime(primarySchedule.getStartTime());
            before.setDurationMinutes(Duration.between(requestStart, primarySchedule.getStartTime()).toMinutes());
            before.setAvailabilityRate(100.0); // 这个时间段设备完全可用
            before.setPriority(1); // 低优先级
            windows.add(before);
        }
        
        // 2. 主任务调度期间的窗口（影随窗口）
        LocalDateTime overlapStart = requestStart.isAfter(primarySchedule.getStartTime()) ? 
            requestStart : primarySchedule.getStartTime();
        LocalDateTime overlapEnd = requestEnd.isBefore(primarySchedule.getEndTime()) ? 
            requestEnd : primarySchedule.getEndTime();
        
        if (!overlapStart.isAfter(overlapEnd)) {
            TimeWindowDTO overlap = new TimeWindowDTO();
            overlap.setStartTime(overlapStart);
            overlap.setEndTime(overlapEnd);
            overlap.setDurationMinutes(Duration.between(overlapStart, overlapEnd).toMinutes());
            overlap.setAvailabilityRate(50.0); // 由于是影随，所以资源部分占用
            overlap.setPriority(3); // 高优先级
            overlap.setRemark("影随时间窗口");
            windows.add(overlap);
        }
        
        // 3. 主任务调度结束后的窗口
        if (requestEnd.isAfter(primarySchedule.getEndTime())) {
            TimeWindowDTO after = new TimeWindowDTO();
            after.setStartTime(primarySchedule.getEndTime());
            after.setEndTime(requestEnd);
            after.setDurationMinutes(Duration.between(primarySchedule.getEndTime(), requestEnd).toMinutes());
            after.setAvailabilityRate(100.0); // 这个时间段设备完全可用
            after.setPriority(1); // 低优先级
            windows.add(after);
        }
        
        return windows;
    }

    @Override
    public ShadowResourceDTO applyShadowResource(Long primaryTaskId, Long shadowTaskId, Long equipmentId,
                                              LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 获取主任务和影随任务
            TaskInfo primaryTask = taskInfoMapper.selectById(primaryTaskId);
            TaskInfo shadowTask = taskInfoMapper.selectById(shadowTaskId);
            if (primaryTask == null || shadowTask == null) {
                log.error("主任务或影随任务不存在");
                return null;
            }

            // 2. 获取设备信息
            GroundEquipment equipment = getEquipmentById(equipmentId);
            if (equipment == null || !isSupportShadowMode(equipment)) {
                log.error("设备不支持影随或设备不存在");
                return null;
            }

            // 3. 获取主任务调度
            TaskSchedule primarySchedule = taskScheduleMapper.getByTaskIdAndEquipmentId(primaryTaskId, equipmentId);
            if (primarySchedule == null) {
                log.error("主任务调度不存在");
                return null;
            }

            // 4. 创建影随任务调度
            TaskSchedule shadowSchedule = new TaskSchedule();
            shadowSchedule.setTaskId(shadowTaskId);
            shadowSchedule.setGroundStationId(equipment.getGroundStationId());
            shadowSchedule.setEquipmentId(equipmentId);
            shadowSchedule.setStartTime(startTime);
            shadowSchedule.setEndTime(endTime);
            shadowSchedule.setHasShadowRelation(true);
            shadowSchedule.setStatus(TaskStatus.PENDING);
            
            // 保存影随任务调度
            taskScheduleMapper.insert(shadowSchedule);

            // 5. 构建返回对象
            ShadowResourceDTO result = new ShadowResourceDTO();
            result.setPrimaryTask(primaryTask);
            result.setShadowTask(shadowTask);
            result.setPrimarySchedule(primarySchedule);
            result.setShadowSchedule(shadowSchedule);
            result.setEquipment(equipment);
            result.setOverlapStart(startTime);
            result.setOverlapEnd(endTime);
            
            return result;

        } catch (Exception e) {
            log.error("申请影随资源出错: ", e);
            return null;
        }
    }

    @Override
    public GroundEquipment findAvailableShadowResource(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 获取影随资源列表
            List<ShadowResourceDTO> shadowResources = findShadowResources(taskInfo, startTime, endTime);
            
            // 如果有可用的影随资源，返回第一个
            if (!shadowResources.isEmpty()) {
                return shadowResources.get(0).getEquipment();
            }
            
            return null;
        } catch (Exception e) {
            log.error("查找可用影随资源失败", e);
            return null;
        }
    }
} 