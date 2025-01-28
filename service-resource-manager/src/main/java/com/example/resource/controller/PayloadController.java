package com.example.resource.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.model.response.Result;
import com.common.model.entity.resource.Payload;
import com.example.resource.service.PayloadService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(tags = "卫星载荷管理接口")
@CrossOrigin@RestController
@RequestMapping("/resource/payloads")
@RequiredArgsConstructor
public class PayloadController {

    private final PayloadService payloadService;

    @ApiOperation("创建载荷")
    @PostMapping
    public Result<Payload> create(@RequestBody Payload payload) {
        return Result.success(payloadService.create(payload));
    }

    @ApiOperation("更新载荷信息")
    @PutMapping("/{id}")
    public Result<Payload> update(@PathVariable Long id, @RequestBody Payload payload) {
        payload.setId(id);
        return Result.success(payloadService.update(payload));
    }

    @ApiOperation("删除载荷")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        payloadService.delete(id);
        return Result.success();
    }

    @ApiOperation("获取载荷详情")
    @GetMapping("/{id}")
    public Result<Payload> getById(@PathVariable Long id) {
        return Result.success(payloadService.getById(id));
    }

    @ApiOperation("分页查询载荷列表")
    @GetMapping
    public Result<IPage<Payload>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long satelliteId,
            @RequestParam(required = false) String status) {
        return Result.success(payloadService.page(new Page<>(current, size), keyword, satelliteId, status));
    }

    @ApiOperation("获取卫星的所有载荷")
    @GetMapping("/satellite/{satelliteId}")
    public Result<IPage<Payload>> getBySatelliteId(
            @PathVariable Long satelliteId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(payloadService.getBySatelliteId(new Page<>(current, size), satelliteId));
    }

    @ApiOperation("更新载荷状态")
    @PutMapping("/{id}/status")
    public Result<Payload> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return Result.success(payloadService.updateStatus(id, status));
    }
} 