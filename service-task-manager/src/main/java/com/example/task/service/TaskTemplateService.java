package com.example.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.task.TaskTemplate;

import java.util.List;

public interface TaskTemplateService extends IService<TaskTemplate> {
    
    TaskTemplate create(TaskTemplate taskTemplate);
    
    TaskTemplate update(TaskTemplate taskTemplate);
    
    void delete(Long id);
    
    IPage<TaskTemplate> page(Page<TaskTemplate> page, String templateName);
    

    TaskTemplate updateStatus(Long id, Boolean isEnabled);
} 