package com.example.resource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.resource.GroundStation;

public interface GroundStationService extends IService<GroundStation> {
    
    GroundStation create(GroundStation groundStation);
    
    GroundStation update(GroundStation groundStation);
    
    void delete(Long id);
    
    IPage<GroundStation> page(Page<GroundStation> page, String keyword, String stationType, String status);
    
    GroundStation updateStatus(Long id, String status);
    
    IPage<GroundStation> getByLocation(Page<GroundStation> page, 
            Double minLatitude, Double maxLatitude, 
            Double minLongitude, Double maxLongitude);
} 