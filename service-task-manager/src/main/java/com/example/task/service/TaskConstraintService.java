package com.example.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.task.TaskConstraint;
import java.util.List;

public interface TaskConstraintService extends IService<TaskConstraint> {

  TaskConstraint create(TaskConstraint constraint);

  TaskConstraint update(TaskConstraint constraint);

  void delete(Long id);

  List<TaskConstraint> getByTaskId(Long templateId);

  void removeByTaskId(Long taskInfoId);

}