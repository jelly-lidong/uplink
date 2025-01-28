package com.example.task.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.model.entity.task.TaskInfo;
import com.common.model.response.Result;
import com.example.task.service.TaskConstraintService;
import com.example.task.service.TaskInfoService;
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


@Api(tags = "任务信息管理接口")
@CrossOrigin@RestController
@RequestMapping("/task/task-infos")
@RequiredArgsConstructor
public class TaskInfoController {

  private final TaskInfoService       taskInfoService;
  private final TaskConstraintService taskConstraintService;

  @ApiOperation("创建任务")
  @PostMapping
  public Result<TaskInfo> create(@RequestBody TaskInfo taskInfo) {
    return Result.success(taskInfoService.create(taskInfo));
  }

  @ApiOperation("更新任务信息")
  @PutMapping("/{id}")
  public Result<TaskInfo> update(@PathVariable Long id, @RequestBody TaskInfo taskInfo) {
    taskInfo.setId(id);
    return Result.success(taskInfoService.update(taskInfo));
  }

  @ApiOperation("删除任务")
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    taskInfoService.delete(id);
    taskConstraintService.removeByTaskId(id);
    return Result.success();
  }

  @ApiOperation("获取任务详情")
  @GetMapping("/{id}")
  public Result<TaskInfo> getById(@PathVariable Long id) {
    return Result.success(taskInfoService.getById(id));
  }

  @ApiOperation("分页查询任务列表")
  @GetMapping
  public Result<IPage<TaskInfo>> page(
      @RequestParam(defaultValue = "1") Integer current,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String taskType,
      @RequestParam(required = false) String status) {
    return Result.success(taskInfoService.page(new Page<>(current, size), name, taskType, status));
  }

  @ApiOperation("根据任务类型查询")
  @GetMapping("/by-type")
  public Result<List<TaskInfo>> getByTaskType(@RequestParam String taskType) {
    return Result.success(taskInfoService.getByTaskType(taskType));
  }
}