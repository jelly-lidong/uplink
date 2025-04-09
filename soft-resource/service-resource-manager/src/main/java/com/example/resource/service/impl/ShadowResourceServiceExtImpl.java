package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.model.dto.ShadowResourceDTO;
import com.common.model.dto.TimeWindowDTO;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.resource.GroundStation;
import com.common.model.entity.task.ShadowRelation;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.common.model.enums.ShadowMode;
import com.common.model.enums.TaskStatus;
import com.example.resource.mapper.ShadowResourceMapper;
import com.example.resource.mapper.TaskInfoMapper;
import com.example.resource.mapper.TaskScheduleMapper;
import com.example.resource.service.GroundEquipmentService;
import com.example.resource.service.GroundStationService;
import com.example.resource.service.ShadowResourceServiceExt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 影随资源服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShadowResourceServiceExtImpl implements ShadowResourceServiceExt {

    private final ShadowResourceMapper shadowResourceMapper;
    private final TaskScheduleMapper taskScheduleMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final GroundEquipmentService groundEquipmentService;
    private final GroundStationService groundStationService;

    @Override
    public List<ShadowResourceDTO> findShadowResources(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime) {
        List<ShadowResourceDTO> result = new ArrayList<>();

        try {
            // 1. 获取当前已有任务调度
            List<TaskSchedule> existingSchedules = shadowResourceMapper.getSchedulesInTimeRange(startTime, endTime);
            if (CollectionUtils.isEmpty(existingSchedules)) {
                log.info("在指定时间范围内没有找到已分配的计划，无法进行影随");
                return result;
            }

            // 2. 遍历已有调度，查找能否影随
            for (TaskSchedule schedule : existingSchedules) {
                // 2.1 获取设备信息
                GroundEquipment equipment = groundEquipmentService.getById(schedule.getEquipmentId());
                if (equipment == null || !equipment.getSupportShadowMode()) {
                    continue; // 设备不支持影随
                }

                // 2.2 检查设备是否支持新任务的类型
                if (!isShadowSupported(equipment, taskInfo)) {
                    continue;
                }

                // 2.3 获取主任务信息
                TaskInfo primaryTask = taskInfoMapper.selectById(schedule.getTaskId());
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
                shadowResource.setGroundStation(groundStationService.getById(equipment.getGroundStationId()));
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
        if (equipment == null || !equipment.getSupportShadowMode()) {
            return false;
        }

        // 检查设备是否支持任务类型
        if (equipment.getSupportedTaskTypes() == null ||
                !equipment.getSupportedTaskTypes().contains(taskInfo.getTaskType())) {
            return false;
        }

        // 检查设备负载状态
        return equipment.getCurrentParallelTasks() < equipment.getMaxParallelTasks();
    }

    @Override
    @Transactional
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
            GroundEquipment equipment = groundEquipmentService.getById(equipmentId);
            if (equipment == null || !isShadowSupported(equipment, shadowTask)) {
                log.error("设备不支持影随或不支持该任务类型");
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
            shadowSchedule.setDescription("影随任务调度 - 主任务ID: " + primaryTaskId);

            // 保存影随任务调度
            taskScheduleMapper.insert(shadowSchedule);

            // 5. 创建影随关系
            ShadowRelation relation = new ShadowRelation();
            relation.setPrimaryScheduleId(primarySchedule.getId());
            relation.setShadowScheduleId(shadowSchedule.getId());
            relation.setShadowMode(equipment.getSupportedTaskTypes().size() > 1 ?
                    ShadowMode.INTEGRATED : getShadowModeByTaskType(shadowTask.getTaskType()));
            relation.setPriority(shadowTask.getPriorityLevel());
            relation.setResourceOccupancy(calculateResourceOccupancy(equipment, primaryTask, shadowTask));
            relation.setSplittable(false); // 默认不可拆分

            // 保存影随关系
            shadowResourceMapper.insert(relation);

            // 6. 更新设备状态
            equipment.setCurrentParallelTasks(equipment.getCurrentParallelTasks() + 1);
            groundEquipmentService.update(equipment);

            // 7. 构建返回对象
            ShadowResourceDTO result = new ShadowResourceDTO();
            result.setPrimaryTask(primaryTask);
            result.setShadowTask(shadowTask);
            result.setPrimarySchedule(primarySchedule);
            result.setShadowSchedule(shadowSchedule);
            result.setEquipment(equipment);
            result.setGroundStation(groundStationService.getById(equipment.getGroundStationId()));
            result.setShadowMode(relation.getShadowMode());
            result.setOverlapStart(startTime);
            result.setOverlapEnd(endTime);
            result.setOverlapDuration(calculateTimeOverlap(startTime, endTime, startTime, endTime));
            result.setResourceOccupancy(relation.getResourceOccupancy());

            return result;

        } catch (Exception e) {
            log.error("申请影随资源出错: ", e);
            // 发生异常时回滚事务
            throw e;
        }
    }

    @Override
    public List<ShadowResourceDTO> getTaskShadowRelations(Long taskId) {
        List<ShadowResourceDTO> result = new ArrayList<>();

        try {
            // 1. 作为主任务查找影随关系
            List<ShadowRelation> primaryRelations = shadowResourceMapper.getShadowRelationsByPrimaryTaskId(taskId);

            for (ShadowRelation relation : primaryRelations) {
                TaskSchedule primarySchedule = taskScheduleMapper.selectById(relation.getPrimaryScheduleId());
                TaskSchedule shadowSchedule = taskScheduleMapper.selectById(relation.getShadowScheduleId());

                if (primarySchedule != null && shadowSchedule != null) {
                    TaskInfo primaryTask = taskInfoMapper.selectById(primarySchedule.getTaskId());
                    TaskInfo shadowTask = taskInfoMapper.selectById(shadowSchedule.getTaskId());
                    GroundEquipment equipment = groundEquipmentService.getById(shadowSchedule.getEquipmentId());

                    if (primaryTask != null && shadowTask != null && equipment != null) {
                        ShadowResourceDTO dto = createShadowResourceDTO(
                                relation, primaryTask, shadowTask, primarySchedule, shadowSchedule, equipment);
                        result.add(dto);
                    }
                }
            }

            // 2. 作为影随任务查找影随关系
            List<ShadowRelation> shadowRelations = shadowResourceMapper.getShadowRelationsByShadowTaskId(taskId);

            for (ShadowRelation relation : shadowRelations) {
                TaskSchedule primarySchedule = taskScheduleMapper.selectById(relation.getPrimaryScheduleId());
                TaskSchedule shadowSchedule = taskScheduleMapper.selectById(relation.getShadowScheduleId());

                if (primarySchedule != null && shadowSchedule != null) {
                    TaskInfo primaryTask = taskInfoMapper.selectById(primarySchedule.getTaskId());
                    TaskInfo shadowTask = taskInfoMapper.selectById(shadowSchedule.getTaskId());
                    GroundEquipment equipment = groundEquipmentService.getById(primarySchedule.getEquipmentId());

                    if (primaryTask != null && shadowTask != null && equipment != null) {
                        ShadowResourceDTO dto = createShadowResourceDTO(
                                relation, primaryTask, shadowTask, primarySchedule, shadowSchedule, equipment);
                        result.add(dto);
                    }
                }
            }

        } catch (Exception e) {
            log.error("获取任务影随关系出错: ", e);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean releaseShadowResource(Long taskScheduleId) {
        try {
            // 1. 获取任务调度信息
            TaskSchedule schedule = taskScheduleMapper.selectById(taskScheduleId);
            if (schedule == null) {
                log.error("任务调度不存在：{}", taskScheduleId);
                return false;
            }

            // 2. 检查是否有影随关系
            if (!Boolean.TRUE.equals(schedule.getHasShadowRelation())) {
                log.warn("该任务调度没有影随关系：{}", taskScheduleId);
                return false;
            }

            // 3. 查找影随关系（作为主任务或影随任务）
            List<ShadowRelation> relations = new ArrayList<>();

            // 查找作为主任务的关系
            LambdaQueryWrapper<ShadowRelation> primaryWrapper = new LambdaQueryWrapper<>();
            primaryWrapper.eq(ShadowRelation::getPrimaryScheduleId, taskScheduleId);
            relations.addAll(shadowResourceMapper.selectList(primaryWrapper));

            // 查找作为影随任务的关系
            LambdaQueryWrapper<ShadowRelation> shadowWrapper = new LambdaQueryWrapper<>();
            shadowWrapper.eq(ShadowRelation::getShadowScheduleId, taskScheduleId);
            relations.addAll(shadowResourceMapper.selectList(shadowWrapper));

            if (relations.isEmpty()) {
                log.warn("未找到与该调度相关的影随关系：{}", taskScheduleId);
                return false;
            }

            // 4. 删除影随关系
            for (ShadowRelation relation : relations) {
                shadowResourceMapper.deleteById(relation.getId());

                // 5. 更新设备状态
                TaskSchedule otherSchedule;
                if (relation.getPrimaryScheduleId().equals(taskScheduleId)) {
                    otherSchedule = taskScheduleMapper.selectById(relation.getShadowScheduleId());
                } else {
                    otherSchedule = taskScheduleMapper.selectById(relation.getPrimaryScheduleId());
                }

                if (otherSchedule != null) {
                    GroundEquipment equipment = groundEquipmentService.getById(otherSchedule.getEquipmentId());
                    if (equipment != null && equipment.getCurrentParallelTasks() > 0) {
                        equipment.setCurrentParallelTasks(equipment.getCurrentParallelTasks() - 1);
                        groundEquipmentService.update(equipment);
                    }
                }
            }

            // 6. 更新任务调度状态
            schedule.setHasShadowRelation(false);
            taskScheduleMapper.updateById(schedule);

            return true;

        } catch (Exception e) {
            log.error("释放影随资源出错：", e);
            throw e;
        }
    }

    @Override
    public List<TaskInfo> findShadowTasks(Long primaryTaskId) {
        List<TaskInfo> shadowTasks = new ArrayList<>();

        try {
            // 获取与主任务相关的影随关系
            List<ShadowRelation> relations = shadowResourceMapper.getShadowRelationsByPrimaryTaskId(primaryTaskId);

            // 获取所有影随任务ID
            for (ShadowRelation relation : relations) {
                TaskSchedule shadowSchedule = taskScheduleMapper.selectById(relation.getShadowScheduleId());
                if (shadowSchedule != null) {
                    TaskInfo shadowTask = taskInfoMapper.selectById(shadowSchedule.getTaskId());
                    if (shadowTask != null) {
                        shadowTasks.add(shadowTask);
                    }
                }
            }

        } catch (Exception e) {
            log.error("查找影随任务出错：", e);
        }

        return shadowTasks;
    }

    @Override
    public TaskInfo findPrimaryTask(Long shadowTaskId) {
        try {
            // 获取与影随任务相关的影随关系
            List<ShadowRelation> relations = shadowResourceMapper.getShadowRelationsByShadowTaskId(shadowTaskId);

            if (!relations.isEmpty()) {
                ShadowRelation relation = relations.get(0); // 获取第一个关系
                TaskSchedule primarySchedule = taskScheduleMapper.selectById(relation.getPrimaryScheduleId());
                if (primarySchedule != null) {
                    return taskInfoMapper.selectById(primarySchedule.getTaskId());
                }
            }

        } catch (Exception e) {
            log.error("查找主任务出错：", e);
        }

        return null;
    }

    @Override
    public List<TaskSchedule> getEquipmentSchedules(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        return shadowResourceMapper.getEquipmentSchedules(equipmentId, startTime, endTime);
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

    @Override
    public List<GroundEquipment> findShadowableEquipmentsByType(Long equipmentId, String taskType) {
        try {
            // 获取所有支持影随的设备
            List<GroundEquipment> allEquipments = findAllShadowSupportedEquipments();

            // 过滤支持指定任务类型的设备
            return allEquipments.stream()
                    .filter(e -> {
                        // 排除自身
                        if (e.getId().equals(equipmentId)) {
                            return false;
                        }

                        // 检查是否支持该任务类型
                        return e.getSupportedTaskTypes() != null &&
                                e.getSupportedTaskTypes().contains(taskType) &&
                                e.getCurrentParallelTasks() < e.getMaxParallelTasks();
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查找可影随设备出错：", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isSupportedTaskType(Long equipmentId, String taskType) {
        GroundEquipment equipment = groundEquipmentService.getById(equipmentId);
        if (equipment == null || !Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
            return false;
        }

        return equipment.getSupportedTaskTypes() != null &&
                equipment.getSupportedTaskTypes().contains(taskType);
    }

    @Override
    public List<TaskInfo> findShadowableTasksByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String taskType) {
        List<TaskInfo> result = new ArrayList<>();

        try {
            // 1. 获取指定时间范围内的调度计划
            List<TaskSchedule> schedules = shadowResourceMapper.getSchedulesInTimeRange(startTime, endTime);
            if (CollectionUtils.isEmpty(schedules)) {
                return result;
            }

            // 2. 获取所有调度对应的任务
            for (TaskSchedule schedule : schedules) {
                // 获取任务信息
                TaskInfo task = taskInfoMapper.selectById(schedule.getTaskId());
                if (task == null) {
                    continue;
                }

                // 获取设备信息
                GroundEquipment equipment = groundEquipmentService.getById(schedule.getEquipmentId());
                if (equipment == null || !Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
                    continue;
                }

                // 检查设备是否支持任务类型
                if (!isSupportedTaskType(equipment.getId(), taskType)) {
                    continue;
                }

                // 检查设备负载
                if (equipment.getCurrentParallelTasks() >= equipment.getMaxParallelTasks()) {
                    continue;
                }

                // 添加到结果
                result.add(task);
            }

        } catch (Exception e) {
            log.error("查找可影随任务出错：", e);
        }

        return result;
    }

    @Override
    public List<ShadowResourceDTO> findPotentialShadowsForTask(Long primaryTaskId) {
        List<ShadowResourceDTO> result = new ArrayList<>();

        try {
            // 1. 获取主任务信息
            TaskInfo primaryTask = taskInfoMapper.selectById(primaryTaskId);
            if (primaryTask == null) {
                log.warn("主任务不存在：{}", primaryTaskId);
                return result;
            }

            // 2. 获取主任务调度计划
            List<TaskSchedule> primarySchedules = taskScheduleMapper.getByTaskId(primaryTaskId);
            if (primarySchedules.isEmpty()) {
                log.warn("主任务没有调度计划：{}", primaryTaskId);
                return result;
            }

            // 3. 对于每个调度计划，查找潜在的影随任务
            for (TaskSchedule primarySchedule : primarySchedules) {
                // 获取设备信息
                GroundEquipment equipment = groundEquipmentService.getById(primarySchedule.getEquipmentId());
                if (equipment == null || !Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
                    continue;
                }

                // 查找所有可能的任务类型
                List<String> supportedTypes = equipment.getSupportedTaskTypes();
                if (supportedTypes == null || supportedTypes.isEmpty()) {
                    continue;
                }

                // 排除主任务自身的类型
                supportedTypes = supportedTypes.stream()
                        .filter(type -> !Objects.equals(type, primaryTask.getTaskType()))
                        .collect(Collectors.toList());

                // 对于每种支持的任务类型，查找合适的任务
                for (String taskType : supportedTypes) {
                    List<TaskInfo> tasksOfType = taskInfoMapper.getByTaskType(taskType);

                    for (TaskInfo potentialShadowTask : tasksOfType) {
                        // 跳过自身
                        if (potentialShadowTask.getId().equals(primaryTaskId)) {
                            continue;
                        }

                        // 检查时间重叠
                        long overlapMinutes = calculateTimeOverlap(
                                primarySchedule.getStartTime(), primarySchedule.getEndTime(),
                                potentialShadowTask.getStartTime(), potentialShadowTask.getEndTime()
                        );

                        if (overlapMinutes <= 0) {
                            continue;
                        }

                        // 创建影随资源DTO
                        ShadowResourceDTO dto = new ShadowResourceDTO();
                        dto.setPrimaryTask(primaryTask);
                        dto.setShadowTask(potentialShadowTask);
                        dto.setPrimarySchedule(primarySchedule);
                        dto.setEquipment(equipment);
                        dto.setGroundStation(groundStationService.getById(equipment.getGroundStationId()));

                        // 设置重叠时间信息
                        LocalDateTime overlapStart = primarySchedule.getStartTime().isAfter(potentialShadowTask.getStartTime()) ?
                                primarySchedule.getStartTime() : potentialShadowTask.getStartTime();
                        LocalDateTime overlapEnd = primarySchedule.getEndTime().isBefore(potentialShadowTask.getEndTime()) ?
                                primarySchedule.getEndTime() : potentialShadowTask.getEndTime();

                        dto.setOverlapStart(overlapStart);
                        dto.setOverlapEnd(overlapEnd);
                        dto.setOverlapDuration(overlapMinutes);

                        // 设置资源占用率和匹配度
                        double resourceOccupancy = calculateResourceOccupancy(equipment, primaryTask, potentialShadowTask);
                        double matchScore = calculateMatchScore(equipment, primaryTask, potentialShadowTask, overlapMinutes);

                        dto.setResourceOccupancy(resourceOccupancy);
                        dto.setMatchScore(matchScore);

                        // 设置影随模式
                        dto.setShadowMode(equipment.getSupportedTaskTypes().size() > 1 ?
                                ShadowMode.INTEGRATED : getShadowModeByTaskType(potentialShadowTask.getTaskType()));

                        result.add(dto);
                    }
                }
            }

            // 按匹配度排序
            result.sort(Comparator.comparing(ShadowResourceDTO::getMatchScore).reversed());

        } catch (Exception e) {
            log.error("查找潜在影随任务出错：", e);
        }

        return result;
    }

    @Override
    public List<TaskInfo> findPotentialPrimaryTasksOnEquipment(Long equipmentId) {
        List<TaskInfo> result = new ArrayList<>();

        try {
            // 1. 获取设备信息
            GroundEquipment equipment = groundEquipmentService.getById(equipmentId);
            if (equipment == null || !Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
                log.warn("设备不支持影随：{}", equipmentId);
                return result;
            }

            // 2. 获取设备上的调度计划
            LocalDateTime now = LocalDateTime.now();
            List<TaskSchedule> schedules = shadowResourceMapper.getEquipmentSchedules(
                    equipmentId, now, now.plusMonths(1)); // 查询当前到未来一个月的调度

            // 3. 获取这些调度对应的任务
            for (TaskSchedule schedule : schedules) {
                TaskInfo task = taskInfoMapper.selectById(schedule.getTaskId());
                if (task != null) {
                    result.add(task);
                }
            }

        } catch (Exception e) {
            log.error("查找潜在主任务出错：", e);
        }

        return result;
    }

    @Override
    public Double calculateEquipmentShadowUtilizationRate(Long equipmentId) {
        try {
            // 1. 获取设备信息
            GroundEquipment equipment = groundEquipmentService.getById(equipmentId);
            if (equipment == null) {
                return 0.0;
            }

            // 2. 如果设备不支持影随，返回0
            if (!Boolean.TRUE.equals(equipment.getSupportShadowMode())) {
                return 0.0;
            }

            // 3. 计算影随利用率
            if (equipment.getMaxParallelTasks() <= 0) {
                return 0.0;
            }

            return (double) equipment.getCurrentParallelTasks() / equipment.getMaxParallelTasks() * 100;

        } catch (Exception e) {
            log.error("计算设备影随资源利用率出错：", e);
            return 0.0;
        }
    }

    @Override
    public List<GroundEquipment> findAllShadowSupportedEquipments() {
        try {
            // 查询所有设备
            List<GroundEquipment> allEquipments = groundEquipmentService.list();

            // 过滤支持影随的设备
            return allEquipments.stream()
                    .filter(e -> Boolean.TRUE.equals(e.getSupportShadowMode()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查找支持影随的设备出错：", e);
            return new ArrayList<>();
        }
    }

    // ========================= 私有辅助方法 =========================

    /**
     * 创建ShadowResourceDTO对象
     */
    private ShadowResourceDTO createShadowResourceDTO(ShadowRelation relation, TaskInfo primaryTask,
                                                      TaskInfo shadowTask, TaskSchedule primarySchedule,
                                                      TaskSchedule shadowSchedule, GroundEquipment equipment) {
        ShadowResourceDTO dto = new ShadowResourceDTO();
        dto.setShadowMode(relation.getShadowMode());
        dto.setPrimaryTask(primaryTask);
        dto.setShadowTask(shadowTask);
        dto.setPrimarySchedule(primarySchedule);
        dto.setShadowSchedule(shadowSchedule);
        dto.setEquipment(equipment);
        dto.setGroundStation(groundStationService.getById(equipment.getGroundStationId()));

        // 设置重叠信息
        LocalDateTime overlapStart = primarySchedule.getStartTime().isAfter(shadowSchedule.getStartTime()) ?
                primarySchedule.getStartTime() : shadowSchedule.getStartTime();
        LocalDateTime overlapEnd = primarySchedule.getEndTime().isBefore(shadowSchedule.getEndTime()) ?
                primarySchedule.getEndTime() : shadowSchedule.getEndTime();

        dto.setOverlapStart(overlapStart);
        dto.setOverlapEnd(overlapEnd);
        dto.setOverlapDuration(calculateTimeOverlap(
                primarySchedule.getStartTime(), primarySchedule.getEndTime(),
                shadowSchedule.getStartTime(), shadowSchedule.getEndTime()));

        dto.setResourceOccupancy(relation.getResourceOccupancy());

        return dto;
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
        double loadScore = 100.0 * (1 - (double) equipment.getCurrentParallelTasks() / equipment.getMaxParallelTasks());
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
}