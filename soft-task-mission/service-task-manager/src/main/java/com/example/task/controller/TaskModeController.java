package com.example.task.controller;

import com.common.model.response.Result;
import com.common.model.entity.task.TaskMode;
import com.example.task.service.TaskModeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "任务模式管理接口")
@CrossOrigin@RestController
@RequestMapping("/task/task-modes")
@RequiredArgsConstructor
public class TaskModeController {

    private final TaskModeService taskModeService;

    @ApiOperation("创建任务模式")
    @PostMapping
    public Result<TaskMode> create(@RequestBody TaskMode taskMode) {
        return Result.success(taskModeService.create(taskMode));
    }

    @ApiOperation("更新任务模式")
    @PutMapping("/{id}")
    public Result<TaskMode> update(@PathVariable Long id, @RequestBody TaskMode taskMode) {
        taskMode.setId(id);
        return Result.success(taskModeService.update(taskMode));
    }

    @ApiOperation("删除任务模式")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        taskModeService.delete(id);
        return Result.success();
    }

    @ApiOperation("获取任务模式详情")
    @GetMapping("/{id}")
    public Result<TaskMode> getById(@PathVariable Long id) {
        return Result.success(taskModeService.getById(id));
    }

    @ApiOperation("查询任务模式列表")
    @GetMapping
    public Result<List<TaskMode>> query(@RequestParam(required = false) Long taskTemplateId,@RequestParam(required = false) String modeName) {
        return Result.success(taskModeService.query(taskTemplateId,modeName));
    }

} 