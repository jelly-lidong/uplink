package com.example.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.task.TaskTemplateConstraint;
import java.util.List;

public interface TaskTemplateConstraintService extends IService<TaskTemplateConstraint> {
    
    /**
     * 创建任务模板约束
     *
     * @param constraint 约束信息
     * @return 创建后的约束信息
     */
    TaskTemplateConstraint create(TaskTemplateConstraint constraint);

    /**
     * 根据模板ID获取约束列表
     *
     * @param taskTemplateId 模板ID
     * @return 约束列表
     */
    List<TaskTemplateConstraint> listByTaskTemplateId(Long taskTemplateId);

  void removeByTaskTemplateId(Long taskTemplateId);

}