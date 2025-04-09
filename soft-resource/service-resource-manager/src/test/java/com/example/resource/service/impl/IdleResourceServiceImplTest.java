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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdleResourceServiceImplTest {
    
    @Mock
    private GroundEquipmentMapper groundEquipmentMapper;
    
    @Mock
    private TaskInfoMapper taskInfoMapper;
    
    @Mock
    private TaskScheduleMapper taskScheduleMapper;
    
    @InjectMocks
    private IdleResourceServiceImpl idleResourceService;
    
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
        equipment.setStatus("IDLE");
    }
    
    @Test
    void findIdleResource_WhenNoEquipmentAvailable_ShouldReturnNull() {
        // 模拟没有可用的设备
        when(groundEquipmentMapper.selectByTaskType(any()))
                .thenReturn(Collections.emptyList());
        
        GroundEquipment result = idleResourceService.findIdleResource(
                taskInfo, startTime, endTime);
        
        assertNull(result);
        verify(groundEquipmentMapper).selectByTaskType(taskInfo.getTaskType());
    }
    
    @Test
    void findIdleResource_WhenEquipmentAvailable_ShouldReturnEquipment() {
        // 模拟有可用的设备
        when(groundEquipmentMapper.selectByTaskType(any()))
                .thenReturn(List.of(equipment));
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        GroundEquipment result = idleResourceService.findIdleResource(
                taskInfo, startTime, endTime);
        
        assertNotNull(result);
        assertEquals(equipment.getId(), result.getId());
        verify(groundEquipmentMapper).selectByTaskType(taskInfo.getTaskType());
    }
    
    @Test
    void allocateIdleResource_WhenEquipmentNotAvailable_ShouldReturnNull() {
        // 模拟设备不可用
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(List.of(new TaskSchedule()));
        
        TaskSchedule result = idleResourceService.allocateIdleResource(
                taskInfo, equipment, startTime, endTime);
        
        assertNull(result);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(
                equipment.getId(), startTime, endTime);
    }
    
    @Test
    void allocateIdleResource_WhenEquipmentNotIdle_ShouldReturnNull() {
        // 模拟设备不空闲
        equipment.setStatus("BUSY");
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        TaskSchedule result = idleResourceService.allocateIdleResource(
                taskInfo, equipment, startTime, endTime);
        
        assertNull(result);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(
                equipment.getId(), startTime, endTime);
    }
    
    @Test
    void allocateIdleResource_WhenEquipmentAvailable_ShouldCreateSchedule() {
        // 模拟设备可用且空闲
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        TaskSchedule result = idleResourceService.allocateIdleResource(
                taskInfo, equipment, startTime, endTime);
        
        assertNotNull(result);
        assertEquals(taskInfo.getId(), result.getTaskId());
        assertEquals(equipment.getGroundStationId(), result.getGroundStationId());
        assertEquals(equipment.getId(), result.getEquipmentId());
        assertEquals(startTime, result.getStartTime());
        assertEquals(endTime, result.getEndTime());
        
        verify(taskScheduleMapper).insert(result);
    }
} 