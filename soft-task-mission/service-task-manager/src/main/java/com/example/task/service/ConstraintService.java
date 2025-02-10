package com.example.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.task.Constraint;

import java.util.List;

public interface ConstraintService extends IService<Constraint> {
    
    Constraint create(Constraint constraint);
    
    Constraint update(Constraint constraint);
    
    void delete(Long id);
    
    IPage<Constraint> page(Page<Constraint> page, String keyword, String constraintType);
    
    List<Constraint> getByConstraintType(String constraintType);
} 