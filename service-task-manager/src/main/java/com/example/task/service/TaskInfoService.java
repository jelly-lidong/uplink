package com.example.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.task.TaskInfo;

import java.util.List;

public interface TaskInfoService extends IService<TaskInfo> {
    
    TaskInfo create(TaskInfo taskInfo);
    
    TaskInfo update(TaskInfo taskInfo);
    
    void delete(Long id);
    
    IPage<TaskInfo> page(Page<TaskInfo> page, String name, String taskType, String status);
    
    List<TaskInfo> getByTaskType(String taskType);
    
}