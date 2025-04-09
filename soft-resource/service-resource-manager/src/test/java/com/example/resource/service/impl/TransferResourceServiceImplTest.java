package com.example.resource.service.impl;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.mapper.GroundEquipmentMapper;
import com.example.resource.mapper.TaskInfoMapper;
import com.example.resource.mapper.TaskScheduleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferResourceServiceImplTest {
    
    @Mock
    private GroundEquipmentMapper groundEquipmentMapper;
    
    @Mock
    private TaskInfoMapper taskInfoMapper;
    
    @Mock
    private TaskScheduleMapper taskScheduleMapper;
    
    @InjectMocks
    private TransferResourceServiceImpl transferResourceService;
    
    private TaskInfo taskInfo;
    private GroundEquipment equipment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now();
        endTime = startTime.plusHours(1);
        
        taskInfo = new TaskInfo();
        taskInfo.setId(1L);
        taskInfo.setTaskType("NORMAL");
        
        equipment = new GroundEquipment();
        equipment.setId(1L);
        equipment.setGroundStationId(1L);
        equipment.setSupportShadowMode(true);
        equipment.setCapabilities(Set.of("capability1", "capability2"));
    }
    
    @Test
    void findTransferableResource_WhenNoEquipmentAvailable_ShouldReturnNull() {
        // 模拟没有可用的设备
        when(groundEquipmentMapper.selectByTaskType(any()))
                .thenReturn(Collections.emptyList());
        
        GroundEquipment result = transferResourceService.findTransferableResource(
                taskInfo, startTime, endTime);
        
        assertNull(result);
        verify(groundEquipmentMapper).selectByTaskType(taskInfo.getTaskType());
    }
    
    @Test
    void findTransferableResource_WhenEquipmentAvailable_ShouldReturnEquipment() {
        // 模拟有可用的设备
        when(groundEquipmentMapper.selectByTaskType(any()))
                .thenReturn(List.of(equipment));
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        GroundEquipment result = transferResourceService.findTransferableResource(
                taskInfo, startTime, endTime);
        
        assertNotNull(result);
        assertEquals(equipment.getId(), result.getId());
        verify(groundEquipmentMapper).selectByTaskType(taskInfo.getTaskType());
    }
    
    @Test
    void transferResource_WhenEquipmentNotAvailable_ShouldReturnNull() {
        // 模拟设备不可用
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(List.of(new TaskSchedule()));
        
        TaskSchedule result = transferResourceService.transferResource(
                taskInfo, equipment, startTime, endTime);
        
        assertNull(result);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(
                equipment.getId(), startTime, endTime);
    }
    
    @Test
    void transferResource_WhenEquipmentAvailable_ShouldCreateSchedule() {
        // 模拟设备可用
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        TaskSchedule result = transferResourceService.transferResource(
                taskInfo, equipment, startTime, endTime);
        
        assertNotNull(result);
        assertEquals(taskInfo.getId(), result.getTaskId());
        assertEquals(equipment.getGroundStationId(), result.getGroundStationId());
        assertEquals(equipment.getId(), result.getEquipmentId());
        assertEquals(startTime, result.getStartTime());
        assertEquals(endTime, result.getEndTime());
        
        verify(taskScheduleMapper).insert(result);
    }
    
    @Test
    void findCompensatoryResources_WhenTaskNotFound_ShouldReturnEmptyList() {
        // 模拟任务不存在
        when(taskInfoMapper.selectById(any()))
                .thenReturn(null);
        
        List<GroundEquipment> result = transferResourceService.findCompensatoryResources(
                1L, 1L, startTime, endTime);
        
        assertTrue(result.isEmpty());
        verify(taskInfoMapper).selectById(1L);
    }
    
    @Test
    void findCompensatoryResources_WhenNoSuitableEquipment_ShouldReturnEmptyList() {
        // 模拟没有合适的设备
        when(taskInfoMapper.selectById(any()))
                .thenReturn(taskInfo);
        when(groundEquipmentMapper.selectByTaskType(any()))
                .thenReturn(Collections.emptyList());
        
        List<GroundEquipment> result = transferResourceService.findCompensatoryResources(
                1L, 1L, startTime, endTime);
        
        assertTrue(result.isEmpty());
        verify(taskInfoMapper).selectById(1L);
        verify(groundEquipmentMapper).selectByTaskType(taskInfo.getTaskType());
    }
    
    @Test
    void findCompensatoryResources_WhenSuitableEquipmentExists_ShouldReturnEquipmentList() {
        // 模拟有合适的设备
        when(taskInfoMapper.selectById(any()))
                .thenReturn(taskInfo);
        when(groundEquipmentMapper.selectByTaskType(any()))
                .thenReturn(List.of(equipment));
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(taskScheduleMapper.selectByTaskId(any()))
                .thenReturn(List.of(new TaskSchedule()));
        
        List<GroundEquipment> result = transferResourceService.findCompensatoryResources(
                2L, 1L, startTime, endTime);
        
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(equipment.getId(), result.get(0).getId());
        
        verify(taskInfoMapper).selectById(1L);
        verify(groundEquipmentMapper).selectByTaskType(taskInfo.getTaskType());
    }
} 