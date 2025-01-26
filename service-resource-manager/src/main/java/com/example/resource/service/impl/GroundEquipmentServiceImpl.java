package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.resource.GroundEquipment;
import com.example.resource.mapper.GroundEquipmentMapper;
import com.example.resource.service.GroundEquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class GroundEquipmentServiceImpl extends ServiceImpl<GroundEquipmentMapper, GroundEquipment> implements GroundEquipmentService {

    @Override
    @Transactional
    public GroundEquipment create(GroundEquipment equipment) {
        save(equipment);
        return equipment;
    }

    @Override
    @Transactional
    public GroundEquipment update(GroundEquipment equipment) {
        updateById(equipment);
        return getById(equipment.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public List<GroundEquipment> query(String keyword, Long groundStationId,
            String equipmentType, String status) {
        LambdaQueryWrapper<GroundEquipment> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(GroundEquipment::getEquipmentName, keyword)
                   .or()
                   .like(GroundEquipment::getEquipmentCode, keyword);
        }
        
        if (groundStationId != null) {
            wrapper.eq(GroundEquipment::getGroundStationId, groundStationId);
        }
        
        if (StringUtils.hasText(equipmentType)) {
            wrapper.eq(GroundEquipment::getEquipmentType, equipmentType);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(GroundEquipment::getStatus, status);
        }
        
        return list(wrapper);
    }

    @Override
    public List<GroundEquipment> getByGroundStationId(Long groundStationId) {
        LambdaQueryWrapper<GroundEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroundEquipment::getGroundStationId, groundStationId);
        return list(wrapper);
    }

    @Override
    @Transactional
    public GroundEquipment updateStatus(Long id, String status) {
        GroundEquipment equipment = getById(id);
        if (equipment == null) {
            throw new RuntimeException("Ground equipment not found");
        }
        
        equipment.setStatus(status);
        updateById(equipment);
        return equipment;
    }

    @Override
    public List<GroundEquipment> getByFrequencyRange(Double minFrequency, Double maxFrequency, String frequencyBand) {
        LambdaQueryWrapper<GroundEquipment> wrapper = new LambdaQueryWrapper<>();
        
        // 频率范围查询
        wrapper.le(GroundEquipment::getMinFrequency, maxFrequency)
               .ge(GroundEquipment::getMaxFrequency, minFrequency);
        
        if (StringUtils.hasText(frequencyBand)) {
            wrapper.eq(GroundEquipment::getFrequencyBand, frequencyBand);
        }
        
        return list(wrapper);
    }
} 