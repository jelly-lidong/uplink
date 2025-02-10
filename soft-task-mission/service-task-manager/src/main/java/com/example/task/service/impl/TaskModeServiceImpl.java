package com.example.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.task.TaskMode;
import com.example.task.mapper.TaskModeMapper;
import com.example.task.service.TaskModeService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskModeServiceImpl extends ServiceImpl<TaskModeMapper, TaskMode> implements TaskModeService {

  @Override
  @Transactional
  public TaskMode create(TaskMode taskMode) {
    save(taskMode);
    return taskMode;
  }

  @Override
  @Transactional
  public TaskMode update(TaskMode taskMode) {
    updateById(taskMode);
    return getById(taskMode.getId());
  }

  @Override
  @Transactional
  public void delete(Long id) {
    removeById(id);
  }


  @Override
  public List<TaskMode> query(Long taskTemplateId, String modename) {
    LambdaQueryWrapper<TaskMode> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(TaskMode::getTaskTemplateId, taskTemplateId)
        .eq(TaskMode::getModeName, modename);
    return this.list(wrapper);
  }

  @Override
  public List<TaskMode> listByTaskTemplateId(Long taskTemplateId) {
    LambdaQueryWrapper<TaskMode> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(TaskMode::getTaskTemplateId, taskTemplateId);
    return this.list(wrapper);
  }

  @Override
  public void removeByTaskTemplateId(Long taskTemplateId) {
    LambdaQueryWrapper<TaskMode> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(TaskMode::getTaskTemplateId, taskTemplateId);
    this.remove(wrapper);
  }
} 