package com.example.resource.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.model.response.Result;
import com.common.model.entity.resource.Satellite;
import com.example.resource.service.SatelliteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "卫星管理接口")
@RestController
@RequestMapping("/resource/satellites")
@RequiredArgsConstructor
public class SatelliteController {

    private final SatelliteService satelliteService;

    @ApiOperation("创建卫星")
    @PostMapping
    public Result<Satellite> create(@RequestBody Satellite satellite) {
        return Result.success(satelliteService.create(satellite));
    }

    @ApiOperation("更新卫星信息")
    @PutMapping("/{id}")
    public Result<Satellite> update(@PathVariable Long id, @RequestBody Satellite satellite) {
        satellite.setId(id);
        return Result.success(satelliteService.update(satellite));
    }

    @ApiOperation("删除卫星")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        satelliteService.delete(id);
        return Result.success();
    }

    @ApiOperation("获取卫星详情")
    @GetMapping("/{id}")
    public Result<Satellite> getById(@PathVariable Long id) {
        return Result.success(satelliteService.getById(id));
    }

    @ApiOperation("分页查询卫星列表")
    @GetMapping
    public Result<IPage<Satellite>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<String> tags) {
        return Result.success(satelliteService.page(new Page<>(current, size), keyword, status, tags));
    }

    @ApiOperation("根据标签查询卫星")
    @GetMapping("/by-tags")
    public Result<List<Satellite>> getByTags(@RequestParam List<String> tags) {
        return Result.success(satelliteService.getByTags(tags));
    }

    @ApiOperation("更新卫星轨道根数")
    @PutMapping("/{id}/orbital-elements")
    public Result<Satellite> updateOrbitalElements(@PathVariable Long id, @RequestBody Satellite satellite) {
        return Result.success(satelliteService.updateOrbitalElements(id, satellite));
    }

    @ApiOperation("更新卫星状态")
    @PutMapping("/{id}/status")
    public Result<Satellite> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return Result.success(satelliteService.updateStatus(id, status));
    }
} 