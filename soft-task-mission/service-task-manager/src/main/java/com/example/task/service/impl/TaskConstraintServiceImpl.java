package com.example.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.task.TaskConstraint;
import com.example.task.mapper.TaskConstraintMapper;
import com.example.task.service.TaskConstraintService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskConstraintServiceImpl extends ServiceImpl<TaskConstraintMapper, TaskConstraint> implements TaskConstraintService {

  @Override
  @Transactional
  public TaskConstraint create(TaskConstraint constraint) {
    save(constraint);
    return constraint;
  }

  @Override
  @Transactional
  public TaskConstraint update(TaskConstraint constraint) {
    updateById(constraint);
    return getById(constraint.getId());
  }

  @Override
  @Transactional
  public void delete(Long id) {
    removeById(id);
  }

  @Override
  public List<TaskConstraint> getByTaskId(Long templateId) {
    LambdaQueryWrapper<TaskConstraint> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(TaskConstraint::getTaskId, templateId);
    return list(wrapper);
  }

  @Override
  public void removeByTaskId(Long taskInfoId) {
    LambdaQueryWrapper<TaskConstraint> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(TaskConstraint::getTaskId, taskInfoId);
    remove(wrapper);
  }

} 