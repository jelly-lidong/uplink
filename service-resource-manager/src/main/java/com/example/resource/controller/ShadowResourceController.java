package com.example.resource.controller;

import com.common.model.dto.ShadowResourceDTO;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.common.model.response.Result;
import com.example.resource.service.ShadowResourceServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 影随资源控制器
 */
@Api(tags = "影随资源管理接口")
@CrossOrigin
@RestController
@RequestMapping("/resource/shadow")
@RequiredArgsConstructor
public class ShadowResourceController {
    
    private final ShadowResourceServiceExt shadowResourceService;
    
    @ApiOperation("查找任务可用的影随资源")
    @PostMapping("/find")
    public Result<List<ShadowResourceDTO>> findShadowResources(
            @RequestBody TaskInfo taskInfo,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(shadowResourceService.findShadowResources(taskInfo, startTime, endTime));
    }
    
    @ApiOperation("申请影随资源")
    @PostMapping("/apply")
    public Result<ShadowResourceDTO> applyShadowResource(
            @ApiParam("主任务ID") @RequestParam Long primaryTaskId,
            @ApiParam("影随任务ID") @RequestParam Long shadowTaskId,
            @ApiParam("设备ID") @RequestParam Long equipmentId,
            @ApiParam("开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(shadowResourceService.applyShadowResource(
            primaryTaskId, shadowTaskId, equipmentId, startTime, endTime));
    }
    
    @ApiOperation("释放影随资源")
    @PostMapping("/release/{taskScheduleId}")
    public Result<Boolean> releaseShadowResource(@PathVariable Long taskScheduleId) {
        return Result.success(shadowResourceService.releaseShadowResource(taskScheduleId));
    }
    
    @ApiOperation("获取任务的影随关系")
    @GetMapping("/task-relations/{taskId}")
    public Result<List<ShadowResourceDTO>> getTaskShadowRelations(@PathVariable Long taskId) {
        return Result.success(shadowResourceService.getTaskShadowRelations(taskId));
    }
    
    @ApiOperation("查找主任务的所有影随任务")
    @GetMapping("/shadow-tasks/{primaryTaskId}")
    public Result<List<TaskInfo>> findShadowTasks(@PathVariable Long primaryTaskId) {
        return Result.success(shadowResourceService.findShadowTasks(primaryTaskId));
    }
    
    @ApiOperation("查找影随任务的主任务")
    @GetMapping("/primary-task/{shadowTaskId}")
    public Result<TaskInfo> findPrimaryTask(@PathVariable Long shadowTaskId) {
        return Result.success(shadowResourceService.findPrimaryTask(shadowTaskId));
    }
    
    @ApiOperation("获取设备在指定时间范围内的调度计划")
    @GetMapping("/equipment-schedules/{equipmentId}")
    public Result<List<TaskSchedule>> getEquipmentSchedules(
            @PathVariable Long equipmentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(shadowResourceService.getEquipmentSchedules(equipmentId, startTime, endTime));
    }
    
    @ApiOperation("查找可用于影随的设备列表")
    @GetMapping("/shadowable-equipments")
    public Result<List<GroundEquipment>> findShadowableEquipmentsByType(
            @ApiParam("设备ID") @RequestParam Long equipmentId,
            @ApiParam("任务类型") @RequestParam String taskType) {
        return Result.success(shadowResourceService.findShadowableEquipmentsByType(equipmentId, taskType));
    }
    
    @ApiOperation("查询设备是否支持指定任务类型")
    @GetMapping("/supported-task-type")
    public Result<Boolean> isSupportedTaskType(
            @ApiParam("设备ID") @RequestParam Long equipmentId,
            @ApiParam("任务类型") @RequestParam String taskType) {
        return Result.success(shadowResourceService.isSupportedTaskType(equipmentId, taskType));
    }
    
    @ApiOperation("根据时间段查询可用于影随的任务")
    @GetMapping("/shadowable-tasks")
    public Result<List<TaskInfo>> findShadowableTasksByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam String taskType) {
        return Result.success(shadowResourceService.findShadowableTasksByTimeRange(startTime, endTime, taskType));
    }
    
    @ApiOperation("根据主任务ID查询可以影随该任务的任务列表")
    @GetMapping("/potential-shadows/{primaryTaskId}")
    public Result<List<ShadowResourceDTO>> findPotentialShadowsForTask(@PathVariable Long primaryTaskId) {
        return Result.success(shadowResourceService.findPotentialShadowsForTask(primaryTaskId));
    }
    
    @ApiOperation("查询设备上可以作为影随目标的任务列表")
    @GetMapping("/potential-primary-tasks/{equipmentId}")
    public Result<List<TaskInfo>> findPotentialPrimaryTasksOnEquipment(@PathVariable Long equipmentId) {
        return Result.success(shadowResourceService.findPotentialPrimaryTasksOnEquipment(equipmentId));
    }
    
    @ApiOperation("统计设备影随资源利用率")
    @GetMapping("/utilization-rate/{equipmentId}")
    public Result<Double> calculateEquipmentShadowUtilizationRate(@PathVariable Long equipmentId) {
        return Result.success(shadowResourceService.calculateEquipmentShadowUtilizationRate(equipmentId));
    }
    
    @ApiOperation("查询所有支持影随的设备列表")
    @GetMapping("/supported-equipments")
    public Result<List<GroundEquipment>> findAllShadowSupportedEquipments() {
        return Result.success(shadowResourceService.findAllShadowSupportedEquipments());
    }
} 