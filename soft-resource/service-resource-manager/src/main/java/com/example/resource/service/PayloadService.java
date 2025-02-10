package com.example.resource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.resource.Payload;

public interface PayloadService extends IService<Payload> {
    
    Payload create(Payload payload);
    
    Payload update(Payload payload);
    
    void delete(Long id);
    
    IPage<Payload> page(Page<Payload> page, String keyword, Long satelliteId, String status);
    
    IPage<Payload> getBySatelliteId(Page<Payload> page, Long satelliteId);
    
    Payload updateStatus(Long id, String status);
} 