package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.resource.GroundStation;
import com.example.resource.mapper.GroundStationMapper;
import com.example.resource.service.GroundStationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class GroundStationServiceImpl extends ServiceImpl<GroundStationMapper, GroundStation> implements GroundStationService {

    @Override
    @Transactional
    public GroundStation create(GroundStation groundStation) {
        save(groundStation);
        return groundStation;
    }

    @Override
    @Transactional
    public GroundStation update(GroundStation groundStation) {
        updateById(groundStation);
        return getById(groundStation.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public IPage<GroundStation> page(Page<GroundStation> page, String keyword, String stationType, String status) {
        LambdaQueryWrapper<GroundStation> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(GroundStation::getStationName, keyword)
                   .or()
                   .like(GroundStation::getStationCode, keyword)
                   .or()
                   .like(GroundStation::getLocation, keyword);
        }
        
        if (StringUtils.hasText(stationType)) {
            wrapper.eq(GroundStation::getStationType, stationType);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(GroundStation::getStatus, status);
        }
        
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public GroundStation updateStatus(Long id, String status) {
        GroundStation station = getById(id);
        if (station == null) {
            throw new RuntimeException("Ground station not found");
        }
        
        station.setStatus(status);
        updateById(station);
        return station;
    }

    @Override
    public IPage<GroundStation> getByLocation(Page<GroundStation> page, 
            Double minLatitude, Double maxLatitude, 
            Double minLongitude, Double maxLongitude) {
        LambdaQueryWrapper<GroundStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(GroundStation::getLatitude, minLatitude, maxLatitude)
               .between(GroundStation::getLongitude, minLongitude, maxLongitude);
        return page(page, wrapper);
    }
} 