package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.model.dto.ShadowResourceDTO;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.mapper.GroundEquipmentMapper;
import com.example.resource.mapper.TaskScheduleMapper;
import com.example.resource.service.*;
import com.example.resource.service.strategy.ResourceApplicationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 一体化资源申请服务实现类
 */
@Slf4j
@Service
public class ResourceApplicationServiceImpl implements ResourceApplicationService {

    @Autowired
    private ShadowResourceService shadowResourceService;

    @Autowired
    private IdleResourceService idleResourceService;

    @Autowired
    private TransferResourceService transferResourceService;

    @Autowired
    private PreemptionResourceService preemptionResourceService;

    @Autowired
    private TaskScheduleMapper taskScheduleMapper;

    @Autowired
    private GroundEquipmentMapper groundEquipmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResourceApplicationResult applyResource(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 尝试申请影随资源
            ResourceApplicationResult result = tryApplyShadowResource(taskInfo, startTime, endTime);
            if (result.isSuccess()) {
                return result;
            }

            // 2. 尝试申请空闲资源
            result = tryApplyIdleResource(taskInfo, startTime, endTime);
            if (result.isSuccess()) {
                return result;
            }

            // 3. 尝试申请可转让资源
            result = tryApplyTransferResource(taskInfo, startTime, endTime);
            if (result.isSuccess()) {
                return result;
            }

            // 4. 尝试申请可抢占资源
            result = tryApplyPreemptionResource(taskInfo, startTime, endTime);
            if (result.isSuccess()) {
                return result;
            }

            // 所有申请方式都失败
            return new ResourceApplicationResult(false, "NONE", null, null, 
                    "所有资源申请方式都失败，无法分配资源");
        } catch (Exception e) {
            log.error("资源申请失败", e);
            return new ResourceApplicationResult(false, "ERROR", null, null, 
                    "资源申请过程中发生异常: " + e.getMessage());
        }
    }

    /**
     * 尝试申请影随资源
     */
    private ResourceApplicationResult tryApplyShadowResource(TaskInfo taskInfo, 
                                                           LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 查找可用的影随资源
            List<ShadowResourceDTO> shadowResourceDTOs = shadowResourceService.findShadowResources(
                    taskInfo, startTime, endTime);
            
            if (shadowResourceDTOs.isEmpty()) {
                return new ResourceApplicationResult(false, "SHADOW", null, null, 
                        "未找到可用的影随资源");
            }
            
            // 2. 获取设备信息
            ShadowResourceDTO selectedDTO = shadowResourceDTOs.get(0);
            GroundEquipment selectedEquipment = groundEquipmentMapper.selectById(selectedDTO.getEquipment().getId());
            
            if (selectedEquipment == null) {
                return new ResourceApplicationResult(false, "SHADOW", null, null, 
                        "设备不存在");
            }
            
            // 3. 创建任务调度
            TaskSchedule taskSchedule = new TaskSchedule();
            taskSchedule.setTaskId(taskInfo.getId());
            taskSchedule.setEquipmentId(selectedEquipment.getId());
            taskSchedule.setStartTime(startTime);
            taskSchedule.setEndTime(endTime);
            taskSchedule.setHasShadowRelation(true);
            taskSchedule.setDescription("影随资源");
            taskScheduleMapper.insert(taskSchedule);
            
            return new ResourceApplicationResult(true, "SHADOW", selectedEquipment, taskSchedule, 
                    "成功申请影随资源");
        } catch (Exception e) {
            log.error("申请影随资源失败", e);
            return new ResourceApplicationResult(false, "SHADOW", null, null, 
                    "申请影随资源失败: " + e.getMessage());
        }
    }

    /**
     * 尝试申请空闲资源
     */
    private ResourceApplicationResult tryApplyIdleResource(TaskInfo taskInfo, 
                                                         LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 查找可用的空闲资源
            List<GroundEquipment> idleResources = idleResourceService.findIdleResources(
                    taskInfo, startTime, endTime);
            
            if (idleResources.isEmpty()) {
                return new ResourceApplicationResult(false, "IDLE", null, null, 
                        "未找到可用的空闲资源");
            }
            
            // 2. 选择第一个可用的空闲资源
            GroundEquipment selectedEquipment = idleResources.get(0);
            
            // 3. 创建任务调度
            TaskSchedule taskSchedule = new TaskSchedule();
            taskSchedule.setTaskId(taskInfo.getId());
            taskSchedule.setEquipmentId(selectedEquipment.getId());
            taskSchedule.setStartTime(startTime);
            taskSchedule.setEndTime(endTime);
            taskSchedule.setDescription("空闲资源");
            taskScheduleMapper.insert(taskSchedule);
            
            return new ResourceApplicationResult(true, "IDLE", selectedEquipment, taskSchedule, 
                    "成功申请空闲资源");
        } catch (Exception e) {
            log.error("申请空闲资源失败", e);
            return new ResourceApplicationResult(false, "IDLE", null, null, 
                    "申请空闲资源失败: " + e.getMessage());
        }
    }

    /**
     * 尝试申请可转让资源
     */
    private ResourceApplicationResult tryApplyTransferResource(TaskInfo taskInfo, 
                                                            LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 查找可转让的资源
            List<GroundEquipment> transferableResources = transferResourceService.findTransferableResources(
                    taskInfo, startTime, endTime);
            
            if (transferableResources.isEmpty()) {
                return new ResourceApplicationResult(false, "TRANSFER", null, null, 
                        "未找到可转让的资源");
            }
            
            // 2. 选择第一个可转让的资源
            GroundEquipment selectedEquipment = transferableResources.get(0);
            
            // 3. 创建任务调度
            TaskSchedule taskSchedule = new TaskSchedule();
            taskSchedule.setTaskId(taskInfo.getId());
            taskSchedule.setEquipmentId(selectedEquipment.getId());
            taskSchedule.setStartTime(startTime);
            taskSchedule.setEndTime(endTime);
            taskSchedule.setDescription("可转让资源");
            taskScheduleMapper.insert(taskSchedule);
            
            return new ResourceApplicationResult(true, "TRANSFER", selectedEquipment, taskSchedule, 
                    "成功申请可转让资源");
        } catch (Exception e) {
            log.error("申请可转让资源失败", e);
            return new ResourceApplicationResult(false, "TRANSFER", null, null, 
                    "申请可转让资源失败: " + e.getMessage());
        }
    }

    /**
     * 尝试申请可抢占资源
     */
    private ResourceApplicationResult tryApplyPreemptionResource(TaskInfo taskInfo, 
                                                              LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 查找可抢占的资源
            List<GroundEquipment> preemptibleResources = preemptionResourceService.findPreemptibleResources(
                    taskInfo, startTime, endTime);
            
            if (preemptibleResources.isEmpty()) {
                return new ResourceApplicationResult(false, "PREEMPTION", null, null, 
                        "未找到可抢占的资源");
            }
            
            // 2. 选择第一个可抢占的资源
            GroundEquipment selectedEquipment = preemptibleResources.get(0);
            
            // 3. 执行资源抢占
            boolean success = preemptionResourceService.executePreemption(
                    taskInfo, null, selectedEquipment.getId(), startTime, endTime);
            
            if (!success) {
                return new ResourceApplicationResult(false, "PREEMPTION", null, null, 
                        "执行资源抢占失败");
            }
            
            // 4. 获取新创建的任务调度
            TaskSchedule taskSchedule = taskScheduleMapper.selectOne(
                    new LambdaQueryWrapper<TaskSchedule>()
                            .eq(TaskSchedule::getTaskId, taskInfo.getId())
                            .orderByDesc(TaskSchedule::getCreateTime)
                            .last("LIMIT 1"));
            
            return new ResourceApplicationResult(true, "PREEMPTION", selectedEquipment, taskSchedule, 
                    "成功申请可抢占资源");
        } catch (Exception e) {
            log.error("申请可抢占资源失败", e);
            return new ResourceApplicationResult(false, "PREEMPTION", null, null, 
                    "申请可抢占资源失败: " + e.getMessage());
        }
    }
} 