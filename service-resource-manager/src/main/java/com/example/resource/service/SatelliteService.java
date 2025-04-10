package com.example.resource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.resource.Satellite;

import java.util.List;

public interface SatelliteService extends IService<Satellite> {
    
    Satellite create(Satellite satellite);
    
    Satellite update(Satellite satellite);
    
    void delete(Long id);
    
    IPage<Satellite> page(Page<Satellite> page, String keyword, String status, List<String> tags);
    
    List<Satellite> getByTags(List<String> tags);
    
    Satellite updateOrbitalElements(Long id, Satellite satellite);
    
    Satellite updateStatus(Long id, String status);
} 