package com.example.task.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.model.entity.task.TaskTemplate;
import com.common.model.response.Result;
import com.example.task.service.TaskTemplateService;
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


@Api(tags = "任务模板管理接口")
@CrossOrigin@RestController
@RequestMapping("/task/task-templates")
@RequiredArgsConstructor
public class TaskTemplateController {

  private final TaskTemplateService taskTemplateService;

  @ApiOperation("创建任务模板")
  @PostMapping
  public Result<TaskTemplate> create(@RequestBody TaskTemplate taskTemplate) {
    return Result.success(taskTemplateService.create(taskTemplate));
  }

  @ApiOperation("更新任务模板")
  @PutMapping("/{id}")
  public Result<TaskTemplate> update(@PathVariable Long id, @RequestBody TaskTemplate taskTemplate) {
    taskTemplate.setId(id);
    return Result.success(taskTemplateService.update(taskTemplate));
  }

  @ApiOperation("删除任务模板")
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    taskTemplateService.delete(id);
    return Result.success();
  }

  @ApiOperation("获取任务模板详情")
  @GetMapping("/{id}")
  public Result<TaskTemplate> getById(@PathVariable Long id) {
    return Result.success(taskTemplateService.getById(id));
  }

  @ApiOperation("分页查询任务模板")
  @GetMapping
  public Result<IPage<TaskTemplate>> page(
      @RequestParam(defaultValue = "1") Integer current,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(required = false) String templateName) {
    return Result.success(taskTemplateService.page(new Page<>(current, size), templateName));
  }


  @ApiOperation("启用/禁用任务模板")
  @PutMapping("/{id}/status")
  public Result<TaskTemplate> updateStatus(@PathVariable Long id, @RequestParam Boolean isEnabled) {
    return Result.success(taskTemplateService.updateStatus(id, isEnabled));
  }
} 