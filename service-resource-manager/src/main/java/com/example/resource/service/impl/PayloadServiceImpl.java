package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.resource.Payload;
import com.example.resource.mapper.PayloadMapper;
import com.example.resource.service.PayloadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PayloadServiceImpl extends ServiceImpl<PayloadMapper, Payload> implements PayloadService {

    @Override
    @Transactional
    public Payload create(Payload payload) {
        save(payload);
        return payload;
    }

    @Override
    @Transactional
    public Payload update(Payload payload) {
        updateById(payload);
        return getById(payload.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public IPage<Payload> page(Page<Payload> page, String keyword, Long satelliteId, String status) {
        LambdaQueryWrapper<Payload> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Payload::getPayloadName, keyword)
                   .or()
                   .like(Payload::getPayloadCode, keyword);
        }
        
        if (satelliteId != null) {
            wrapper.eq(Payload::getSatelliteId, satelliteId);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Payload::getStatus, status);
        }
        
        return page(page, wrapper);
    }

    @Override
    public IPage<Payload> getBySatelliteId(Page<Payload> page, Long satelliteId) {
        LambdaQueryWrapper<Payload> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payload::getSatelliteId, satelliteId);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public Payload updateStatus(Long id, String status) {
        Payload payload = getById(id);
        if (payload == null) {
            throw new RuntimeException("Payload not found");
        }
        
        payload.setStatus(status);
        updateById(payload);
        return payload;
    }
} 