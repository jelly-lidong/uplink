package com.example.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.task.TaskMode;
import com.common.model.entity.task.TaskTemplate;
import com.common.model.entity.task.TaskTemplateConstraint;
import com.example.task.mapper.TaskTemplateMapper;
import com.example.task.service.TaskModeService;
import com.example.task.service.TaskTemplateConstraintService;
import com.example.task.service.TaskTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class TaskTemplateServiceImpl extends ServiceImpl<TaskTemplateMapper, TaskTemplate> implements TaskTemplateService {

  private final TaskModeService taskModeService;

  private final TaskTemplateConstraintService taskTemplateConstraintService;

  @Override
  @Transactional
  public TaskTemplate create(TaskTemplate taskTemplate) {
    save(taskTemplate);
    for (TaskMode taskMode : taskTemplate.getTaskModes()) {
      taskMode.setTaskTemplateId(taskTemplate.getId());
      taskModeService.create(taskMode);
    }
    for (TaskTemplateConstraint constraint : taskTemplate.getTaskTemplateConstraints()) {
      constraint.setTaskTemplateId(taskTemplate.getId());
      taskTemplateConstraintService.create(constraint);
    }
    return taskTemplate;
  }

  @Override
  @Transactional
  public TaskTemplate update(TaskTemplate taskTemplate) {
    updateById(taskTemplate);

    taskModeService.removeByTaskTemplateId(taskTemplate.getId());
    for (TaskMode taskMode : taskTemplate.getTaskModes()) {
      taskMode.setTaskTemplateId(taskTemplate.getId());
      taskModeService.saveOrUpdate(taskMode);
    }
    taskTemplateConstraintService.removeByTaskTemplateId(taskTemplate.getId());
    for (TaskTemplateConstraint constraint : taskTemplate.getTaskTemplateConstraints()) {
      constraint.setTaskTemplateId(taskTemplate.getId());
      taskTemplateConstraintService.saveOrUpdate(constraint);
    }
    return getById(taskTemplate.getId());
  }

  @Override
  @Transactional
  public void delete(Long id) {
    removeById(id);
    taskModeService.removeByTaskTemplateId(id);
    taskTemplateConstraintService.removeByTaskTemplateId(id);
  }

  @Override
  public IPage<TaskTemplate> page(Page<TaskTemplate> page, String templateName) {
    LambdaQueryWrapper<TaskTemplate> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(StringUtils.hasText(templateName), TaskTemplate::getTemplateName, templateName);
    Page<TaskTemplate> taskTemplatePage = page(page, wrapper);

    for (TaskTemplate taskTemplate : taskTemplatePage.getRecords()) {
      taskTemplate.setTaskModes(taskModeService.listByTaskTemplateId(taskTemplate.getId()));
      taskTemplate.setTaskTemplateConstraints(taskTemplateConstraintService.listByTaskTemplateId(taskTemplate.getId()));
    }


    return taskTemplatePage;
  }


  @Override
  @Transactional
  public TaskTemplate updateStatus(Long id, Boolean isEnabled) {
    TaskTemplate template = getById(id);
    if (template == null) {
      throw new RuntimeException("Task template not found");
    }
    template.setIsEnabled(isEnabled);
    updateById(template);
    return template;
  }
} 