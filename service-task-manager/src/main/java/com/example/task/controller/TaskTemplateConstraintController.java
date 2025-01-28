package com.example.task.controller;

import com.common.model.entity.task.TaskTemplateConstraint;
import com.common.model.response.Result;
import com.example.task.service.TaskTemplateConstraintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "任务模板约束管理")
@CrossOrigin@RestController
@RequestMapping("/task/template/constraint")
@RequiredArgsConstructor
public class TaskTemplateConstraintController {

    private final TaskTemplateConstraintService taskTemplateConstraintService;

    @Operation(summary = "创建任务模板约束")
    @PostMapping
    public Result<TaskTemplateConstraint> create(@RequestBody TaskTemplateConstraint constraint) {
        return Result.success(taskTemplateConstraintService.create(constraint));
    }

    @Operation(summary = "更新任务模板约束")
    @PutMapping
    public Result<Boolean> update(@RequestBody TaskTemplateConstraint constraint) {
        return Result.success(taskTemplateConstraintService.updateById(constraint));
    }

    @Operation(summary = "删除任务模板约束")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(taskTemplateConstraintService.removeById(id));
    }

    @Operation(summary = "获取任务模板约束详情")
    @GetMapping("/{id}")
    public Result<TaskTemplateConstraint> getById(@PathVariable Long id) {
        return Result.success(taskTemplateConstraintService.getById(id));
    }

    @Operation(summary = "获取任务模板的所有约束")
    @GetMapping("/template/{templateId}")
    public Result<List<TaskTemplateConstraint>> getByTemplateId(@PathVariable Long templateId) {
        return Result.success(taskTemplateConstraintService.listByTaskTemplateId(templateId));
    }
} 