package com.example.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.task.TaskConstraint;
import com.common.model.entity.task.TaskInfo;
import com.example.task.mapper.TaskInfoMapper;
import com.example.task.service.TaskConstraintService;
import com.example.task.service.TaskInfoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aircas.orbit.util.TreeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo> implements TaskInfoService {

  private final TaskConstraintService taskConstraintService;

  @Override
  @Transactional
  public TaskInfo create(TaskInfo taskInfo) {
    save(taskInfo);
    List<TaskConstraint> taskConstraints = taskInfo.getTaskConstraints();
    taskConstraintService.removeByTaskId(taskInfo.getId());
    if (CollectionUtils.isNotEmpty(taskConstraints)) {
      for (TaskConstraint taskConstraint : taskConstraints) {
        taskConstraint.setId(null);
        taskConstraint.setTaskId(taskInfo.getId());
      }
      taskConstraintService.saveBatch(taskConstraints);
    }
    return taskInfo;
  }

  @Override
  @Transactional
  public TaskInfo update(TaskInfo taskInfo) {
    updateById(taskInfo);
    List<TaskConstraint> taskConstraints = taskInfo.getTaskConstraints();
    taskConstraintService.removeByTaskId(taskInfo.getId());
    if (CollectionUtils.isNotEmpty(taskConstraints)) {
      for (TaskConstraint taskConstraint : taskConstraints) {
        taskConstraint.setTaskId(taskInfo.getId());
      }
    }
    taskConstraintService.saveBatch(taskConstraints);
    return getById(taskInfo.getId());
  }

  @Override
  @Transactional
  public void delete(Long id) {
    removeById(id);
  }

  @Override
  public IPage<TaskInfo> page(Page<TaskInfo> page, String keyword, String taskType, String status) {
    LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(keyword)) {
      wrapper.like(TaskInfo::getTaskName, keyword);
    }

    if (StringUtils.hasText(taskType)) {
      wrapper.eq(TaskInfo::getTaskType, taskType);
    }

    if (StringUtils.hasText(status)) {
      wrapper.eq(TaskInfo::getTaskStatus, status);
    }
    Page<TaskInfo> infoPage = page(page, wrapper);
    for (TaskInfo taskInfo : infoPage.getRecords()) {
      List<TaskConstraint> constraintList = taskConstraintService.getByTaskId(taskInfo.getId());
      taskInfo.setTaskConstraints(constraintList);
    }
    // 构建树形结构
    List<TaskInfo> tree = TreeUtil.makeTree(
        infoPage.getRecords(),
        // 判断根节点:parentId为null或0的为根节点
        task -> task.getParentId() == null || task.getParentId() == 0L,
        // 判断父子关系:当前节点的parentId等于父节点的id
        (parent, child) -> parent.getId().equals(child.getParentId()),
        // 设置子节点
        TaskInfo::setChildren
    );
    infoPage.setRecords(tree);
    return infoPage;
  }

  @Override
  public List<TaskInfo> getByTaskType(String taskType) {
    LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(TaskInfo::getTaskType, taskType);
    return list(wrapper);
  }


} 