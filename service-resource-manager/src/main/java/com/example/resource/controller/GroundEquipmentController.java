package com.example.resource.controller;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.EquipmentCapability;
import com.common.model.response.Result;
import com.example.resource.service.GroundEquipmentService;
import com.example.resource.service.ShadowResourceServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "地面站装备管理接口")
@CrossOrigin
@RestController
@RequestMapping("/resource/ground-equipments")
@RequiredArgsConstructor
public class GroundEquipmentController {

  private final GroundEquipmentService groundEquipmentService;
  private final ShadowResourceServiceExt shadowResourceService;

  @ApiOperation("创建装备")
  @PostMapping
  public Result<GroundEquipment> create(@RequestBody GroundEquipment equipment) {
    return Result.success(groundEquipmentService.create(equipment));
  }

  @ApiOperation("更新装备信息")
  @PutMapping("/{id}")
  public Result<GroundEquipment> update(@PathVariable Long id, @RequestBody GroundEquipment equipment) {
    equipment.setId(id);
    return Result.success(groundEquipmentService.update(equipment));
  }

  @ApiOperation("删除装备")
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    groundEquipmentService.delete(id);
    return Result.success();
  }

  @ApiOperation("获取装备详情")
  @GetMapping("/{id}")
  public Result<GroundEquipment> getById(@PathVariable Long id) {
    return Result.success(groundEquipmentService.getById(id));
  }

  @ApiOperation("查询装备列表")
  @GetMapping
  public Result<List<GroundEquipment>> page(
      @RequestParam(required = false) Long groundStationId,
      @RequestParam(required = false) String equipmentType,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status) {
    return Result.success(groundEquipmentService.query(keyword, groundStationId, equipmentType, status));
  }

  @ApiOperation("获取地面站的所有装备")
  @GetMapping("/ground-station/{groundStationId}")
  public Result<List<GroundEquipment>> getByGroundStationId(@PathVariable Long groundStationId) {
    return Result.success(groundEquipmentService.getByGroundStationId(groundStationId));
  }

  @ApiOperation("更新装备状态")
  @PutMapping("/{id}/status")
  public Result<GroundEquipment> updateStatus(@PathVariable Long id, @RequestParam String status) {
    return Result.success(groundEquipmentService.updateStatus(id, status));
  }

  @ApiOperation("根据频段查询装备")
  @GetMapping("/by-frequency")
  public Result<List<GroundEquipment>> getByFrequencyRange(
      @RequestParam Double minFrequency,
      @RequestParam Double maxFrequency,
      @RequestParam(required = false) String frequencyBand) {
    return Result.success(groundEquipmentService.getByFrequencyRange(minFrequency, maxFrequency, frequencyBand));
  }

  @ApiOperation("更新设备影随能力")
  @PutMapping("/{id}/shadow-capability")
  public Result<GroundEquipment> updateShadowCapability(
      @PathVariable Long id,
      @RequestParam Boolean supportShadowMode,
      @RequestBody(required = false) List<String> supportedTaskTypes,
      @RequestParam(required = false, defaultValue = "1") Integer maxParallelTasks) {
    return Result.success(groundEquipmentService.updateShadowCapability(
        id, supportShadowMode, supportedTaskTypes, maxParallelTasks));
  }

  @ApiOperation("获取设备影随能力详情")
  @GetMapping("/{id}/capability")
  public Result<EquipmentCapability> getEquipmentCapability(@PathVariable Long id) {
    return Result.success(groundEquipmentService.getEquipmentCapability(id));
  }

  @ApiOperation("更新设备当前任务数")
  @PutMapping("/{id}/task-count")
  public Result<GroundEquipment> updateTaskCount(
      @PathVariable Long id,
      @RequestParam Integer count) {
    return Result.success(groundEquipmentService.updateTaskCount(id, count));
  }

  @ApiOperation("查询所有支持影随的设备")
  @GetMapping("/shadow-supported")
  public Result<List<GroundEquipment>> findAllShadowSupportedEquipments() {
    return Result.success(((ShadowResourceServiceExt) shadowResourceService).findAllShadowSupportedEquipments());
  }
} 