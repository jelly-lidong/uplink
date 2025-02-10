package com.example.task.controller;

import com.common.model.response.Result;
import com.common.model.entity.task.TaskConstraint;
import com.example.task.service.TaskConstraintService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "任务约束管理接口")
@CrossOrigin@RestController
@RequestMapping("/task/task-constraints")
@RequiredArgsConstructor
public class TaskConstraintController {

    private final TaskConstraintService taskConstraintService;

    @ApiOperation("创建任务约束")
    @PostMapping
    public Result<TaskConstraint> create(@RequestBody TaskConstraint constraint) {
        return Result.success(taskConstraintService.create(constraint));
    }

    @ApiOperation("更新任务约束")
    @PutMapping("/{id}")
    public Result<TaskConstraint> update(@PathVariable Long id, @RequestBody TaskConstraint constraint) {
        constraint.setId(id);
        return Result.success(taskConstraintService.update(constraint));
    }

    @ApiOperation("删除任务约束")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        taskConstraintService.delete(id);
        return Result.success();
    }

    @ApiOperation("获取任务约束详情")
    @GetMapping("/{id}")
    public Result<TaskConstraint> getById(@PathVariable Long id) {
        return Result.success(taskConstraintService.getById(id));
    }

    @ApiOperation("获取任务所有约束")
    @GetMapping("/template/{taskId}")
    public Result<List<TaskConstraint>> getByTemplateId(@PathVariable Long taskId) {
        return Result.success(taskConstraintService.getByTaskId(taskId));
    }

} 