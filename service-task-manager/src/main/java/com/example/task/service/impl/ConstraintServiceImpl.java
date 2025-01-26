package com.example.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.task.Constraint;
import com.example.task.mapper.ConstraintMapper;
import com.example.task.service.ConstraintService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ConstraintServiceImpl extends ServiceImpl<ConstraintMapper, Constraint> implements ConstraintService {

    @Override
    @Transactional
    public Constraint create(Constraint constraint) {
        save(constraint);
        return constraint;
    }

    @Override
    @Transactional
    public Constraint update(Constraint constraint) {
        updateById(constraint);
        return getById(constraint.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public IPage<Constraint> page(Page<Constraint> page, String keyword, String constraintType) {
        LambdaQueryWrapper<Constraint> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Constraint::getName, keyword);
        }
        
        if (StringUtils.hasText(constraintType)) {
            wrapper.eq(Constraint::getConstraintType, constraintType);
        }
        
        return page(page, wrapper);
    }

    @Override
    public List<Constraint> getByConstraintType(String constraintType) {
        LambdaQueryWrapper<Constraint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Constraint::getConstraintType, constraintType);
        return list(wrapper);
    }
} 