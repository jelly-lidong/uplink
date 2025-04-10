package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.EquipmentCapability;
import com.common.model.entity.task.TaskInfo;
import com.common.model.enums.ShadowMode;
import com.example.resource.mapper.GroundEquipmentMapper;
import com.example.resource.service.GroundEquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.util.StringUtils;

import java.util.List;
import java.time.LocalDateTime;

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
        LambdaQueryWrapper<GroundEquipment> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.le(GroundEquipment::getMinFrequency, maxFrequency)
                .ge(GroundEquipment::getMaxFrequency, minFrequency);
        
        if (!StringUtils.isEmpty(frequencyBand)) {
            queryWrapper.eq(GroundEquipment::getFrequencyBand, frequencyBand);
        }
        
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public GroundEquipment updateShadowCapability(Long equipmentId, Boolean supportShadowMode, 
                                                List<String> supportedTaskTypes, Integer maxParallelTasks) {
        GroundEquipment equipment = getById(equipmentId);
        if (equipment == null) {
            throw new RuntimeException("设备不存在");
        }
        
        // 更新影随能力相关属性
        equipment.setSupportShadowMode(supportShadowMode);
        equipment.setSupportedTaskTypes(supportedTaskTypes);
        equipment.setMaxParallelTasks(maxParallelTasks);
        
        // 如果不支持影随，清空当前并行任务数
        if (!Boolean.TRUE.equals(supportShadowMode)) {
            equipment.setCurrentParallelTasks(0);
        } else if (equipment.getCurrentParallelTasks() == null) {
            equipment.setCurrentParallelTasks(0); // 初始化当前任务数
        }
        
        // 更新设备信息
        updateById(equipment);
        
        // 更新或创建设备能力记录
        updateEquipmentCapabilityRecord(equipment);
        
        return equipment;
    }
    
    @Override
    public EquipmentCapability getEquipmentCapability(Long equipmentId) {
        // 查询设备能力表
        // 这里假设有一个EquipmentCapabilityMapper，需要按项目实际情况实现
        // 如果没有单独的Mapper，可以在这里创建一个新的能力对象并填充数据
        
        GroundEquipment equipment = getById(equipmentId);
        if (equipment == null) {
            return null;
        }
        
        // 创建并返回能力对象
        EquipmentCapability capability = new EquipmentCapability();
        capability.setEquipmentId(equipmentId);
        capability.setEquipmentName(equipment.getEquipmentName());
        
        // 设置影随模式
        if (Boolean.TRUE.equals(equipment.getSupportShadowMode()) && 
            equipment.getSupportedTaskTypes() != null && 
            equipment.getSupportedTaskTypes().size() > 1) {
            capability.setShadowMode(ShadowMode.INTEGRATED);
        } else if (Boolean.TRUE.equals(equipment.getSupportShadowMode()) && 
                  equipment.getSupportedTaskTypes() != null && 
                  !equipment.getSupportedTaskTypes().isEmpty()) {
            // 根据支持的第一个任务类型设置模式
            String firstType = equipment.getSupportedTaskTypes().get(0);
            if ("测控".equals(firstType)) {
                capability.setShadowMode(ShadowMode.MEASUREMENT_CONTROL);
            } else if ("数传".equals(firstType)) {
                capability.setShadowMode(ShadowMode.DATA_TRANSMISSION);
            } else {
                capability.setShadowMode(ShadowMode.INTEGRATED);
            }
        } else {
            capability.setShadowMode(ShadowMode.NONE);
        }
        
        capability.setMaxConcurrentTasks(equipment.getMaxParallelTasks());
        capability.setCurrentTaskCount(equipment.getCurrentParallelTasks());
        capability.setSupportedTaskTypes(equipment.getSupportedTaskTypes());
        
        // 计算负载
        if (equipment.getMaxParallelTasks() != null && equipment.getMaxParallelTasks() > 0) {
            double load = (double) equipment.getCurrentParallelTasks() / equipment.getMaxParallelTasks() * 100;
            capability.setCurrentLoad(load);
            capability.setLoadThreshold(80.0); // 默认阈值
        }
        
        // 设置设备状态
        capability.setStatus(equipment.getStatus());
        
        return capability;
    }
    
    @Override
    @Transactional
    public GroundEquipment updateTaskCount(Long equipmentId, Integer count) {
        GroundEquipment equipment = getById(equipmentId);
        if (equipment == null) {
            throw new RuntimeException("设备不存在");
        }
        
        // 更新当前任务数
        int currentCount = equipment.getCurrentParallelTasks() == null ? 0 : equipment.getCurrentParallelTasks();
        int newCount = currentCount + count;
        
        // 确保不小于0
        if (newCount < 0) {
            newCount = 0;
        }
        
        // 确保不超过最大值
        if (equipment.getMaxParallelTasks() != null && newCount > equipment.getMaxParallelTasks()) {
            newCount = equipment.getMaxParallelTasks();
        }
        
        equipment.setCurrentParallelTasks(newCount);
        updateById(equipment);
        
        return equipment;
    }
    
    /**
     * 更新设备能力记录
     */
    private void updateEquipmentCapabilityRecord(GroundEquipment equipment) {
        // 实现更新设备能力记录的逻辑
        // 这里需要根据实际项目情况完成实现
    }
    
    @Override
    public List<GroundEquipment> findAvailableCoreResources(TaskInfo task, LocalDateTime startTime, LocalDateTime endTime) {
        // 查询所有支持该任务类型的核心资源
        LambdaQueryWrapper<GroundEquipment> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(GroundEquipment::getEquipmentType, "CORE")
                    .eq(GroundEquipment::getStatus, "ONLINE");
        
        // 如果任务有特定类型，筛选支持该类型的设备
        if (task.getTaskType() != null) {
            queryWrapper.apply("JSON_CONTAINS(supported_task_types, '{\"" + task.getTaskType() + "\"}')");
        }
        
        // 获取所有候选设备
        List<GroundEquipment> candidateEquipments = this.baseMapper.selectList(queryWrapper);
        
        // 过滤掉在指定时间段内已被占用的设备
        return candidateEquipments.stream()
                .filter(equipment -> isEquipmentAvailable(equipment.getId(), startTime, endTime))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 检查设备在指定时间段是否可用
     */
    private boolean isEquipmentAvailable(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        // 这里需要根据实际业务逻辑进行检查
        // 例如查询任务调度表，看是否有重叠的时间段
        
        // 简化实现，实际项目中应该查询数据库判断
        return true;
    }
} 