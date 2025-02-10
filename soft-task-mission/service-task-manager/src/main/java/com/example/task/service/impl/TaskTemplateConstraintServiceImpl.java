package com.example.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.task.TaskTemplateConstraint;
import com.example.task.mapper.TaskTemplateConstraintMapper;
import com.example.task.service.TaskTemplateConstraintService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskTemplateConstraintServiceImpl
    extends ServiceImpl<TaskTemplateConstraintMapper, TaskTemplateConstraint>
    implements TaskTemplateConstraintService {

  @Override
  public TaskTemplateConstraint create(TaskTemplateConstraint constraint) {
    save(constraint);
    return constraint;
  }

  @Override
  public List<TaskTemplateConstraint> listByTaskTemplateId(Long taskTemplateId) {
    return list(new LambdaQueryWrapper<TaskTemplateConstraint>()
        .eq(TaskTemplateConstraint::getTaskTemplateId, taskTemplateId));
  }

  @Override
  public void removeByTaskTemplateId(Long taskTemplateId) {
    remove(new LambdaQueryWrapper<TaskTemplateConstraint>()
        .eq(TaskTemplateConstraint::getTaskTemplateId, taskTemplateId));
  }
} 