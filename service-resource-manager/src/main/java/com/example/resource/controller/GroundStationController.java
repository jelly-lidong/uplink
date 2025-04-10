package com.example.resource.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.model.entity.resource.GroundStation;
import com.common.model.response.Result;
import com.example.resource.service.GroundStationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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


@Api(tags = "地面站管理接口")

@CrossOrigin
@RestController
@RequestMapping("/resource/ground-stations")
@RequiredArgsConstructor
public class GroundStationController {

  private final GroundStationService groundStationService;

  @ApiOperation("创建地面站")
  @PostMapping
  public Result<GroundStation> create(@RequestBody GroundStation groundStation) {
    return Result.success(groundStationService.create(groundStation));
  }

  @ApiOperation("更新地面站信息")
  @PutMapping("/{id}")
  public Result<GroundStation> update(@PathVariable Long id, @RequestBody GroundStation groundStation) {
    groundStation.setId(id);
    return Result.success(groundStationService.update(groundStation));
  }

  @ApiOperation("删除地面站")
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    groundStationService.delete(id);
    return Result.success();
  }

  @ApiOperation("获取地面站详情")
  @GetMapping("/{id}")
  public Result<GroundStation> getById(@PathVariable Long id) {
    return Result.success(groundStationService.getById(id));
  }

  @ApiOperation("分页查询地面站列表")
  @GetMapping
  public Result<IPage<GroundStation>> page(
      @RequestParam(defaultValue = "1") Integer current,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String stationType,
      @RequestParam(required = false) String status) {
    return Result.success(groundStationService.page(new Page<>(current, size), keyword, stationType, status));
  }

  @ApiOperation("更新地面站状态")
  @PutMapping("/{id}/status")
  public Result<GroundStation> updateStatus(@PathVariable Long id, @RequestParam String status) {
    return Result.success(groundStationService.updateStatus(id, status));
  }

  @ApiOperation("根据经纬度范围查询地面站")
  @GetMapping("/by-location")
  public Result<IPage<GroundStation>> getByLocation(
      @RequestParam Double minLatitude,
      @RequestParam Double maxLatitude,
      @RequestParam Double minLongitude,
      @RequestParam Double maxLongitude,
      @RequestParam(defaultValue = "1") Integer current,
      @RequestParam(defaultValue = "10") Integer size) {
    return Result.success(groundStationService.getByLocation(new Page<>(current, size),
        minLatitude, maxLatitude, minLongitude, maxLongitude));
  }
} 