package com.example.resource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.model.entity.resource.GroundEquipment;

import java.util.List;

public interface GroundEquipmentService extends IService<GroundEquipment> {
    
    GroundEquipment create(GroundEquipment equipment);
    
    GroundEquipment update(GroundEquipment equipment);
    
    void delete(Long id);
    
    List<GroundEquipment> query(String keyword, Long groundStationId, String equipmentType, String status);
    
    List<GroundEquipment> getByGroundStationId(Long groundStationId);
    
    GroundEquipment updateStatus(Long id, String status);
    
    List<GroundEquipment> getByFrequencyRange(Double minFrequency, Double maxFrequency, String frequencyBand);
} 