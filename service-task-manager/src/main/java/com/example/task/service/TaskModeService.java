package com.example.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.task.TaskMode;

import java.util.List;

public interface TaskModeService extends IService<TaskMode> {
    
    TaskMode create(TaskMode taskMode);
    
    TaskMode update(TaskMode taskMode);
    
    void delete(Long id);
    

    List<TaskMode> query(Long taskTemplateId, String modeName);

    List<TaskMode> listByTaskTemplateId(Long taskTemplateId);

  void removeByTaskTemplateId(Long taskTemplateId);
}