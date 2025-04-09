package com.example.resource.service.impl;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.mapper.TaskInfoMapper;
import com.example.resource.mapper.TaskScheduleMapper;
import com.example.resource.model.TaskFeature;
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
class PreemptionResourceServiceImplTest {
    
    @Mock
    private TaskInfoMapper taskInfoMapper;
    
    @Mock
    private TaskScheduleMapper taskScheduleMapper;
    
    @InjectMocks
    private PreemptionResourceServiceImpl preemptionResourceService;
    
    private TaskFeature taskFeature;
    private TaskInfo taskInfo;
    private GroundEquipment equipment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now();
        endTime = startTime.plusHours(1);
        
        taskFeature = TaskFeature.builder()
                .taskType("EMERGENCY")
                .priorityLevel(3)
                .emergency(true)
                .build();
        
        taskInfo = new TaskInfo();
        taskInfo.setTaskId("task-1");
        taskInfo.setTaskType("EMERGENCY");
        taskInfo.setPriorityLevel(3);
        
        equipment = new GroundEquipment();
        equipment.setEquipmentId("equipment-1");
        equipment.setGroundStationId("station-1");
    }
    
    @Test
    void findPreemptibleResource_WhenNoEquipmentAvailable_ShouldReturnNull() {
        // 模拟没有可用的设备
        when(taskInfoMapper.findPreemptibleEquipments(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        GroundEquipment result = preemptionResourceService.findPreemptibleResource(
                taskFeature, startTime, endTime);
        
        assertNull(result);
        verify(taskInfoMapper).findPreemptibleEquipments(
                taskFeature.getTaskType(),
                taskFeature.getPriorityLevel(),
                startTime,
                endTime
        );
    }
    
    @Test
    void findPreemptibleResource_WhenEquipmentAvailable_ShouldReturnEquipment() {
        // 模拟有可用的设备
        when(taskInfoMapper.findPreemptibleEquipments(any(), any(), any(), any()))
                .thenReturn(List.of(equipment));
        when(taskScheduleMapper.findByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        GroundEquipment result = preemptionResourceService.findPreemptibleResource(
                taskFeature, startTime, endTime);
        
        assertNotNull(result);
        assertEquals(equipment.getEquipmentId(), result.getEquipmentId());
        verify(taskInfoMapper).findPreemptibleEquipments(
                taskFeature.getTaskType(),
                taskFeature.getPriorityLevel(),
                startTime,
                endTime
        );
    }
    
    @Test
    void preemptResource_WhenEquipmentNotAvailable_ShouldReturnNull() {
        // 模拟设备不可用
        when(taskScheduleMapper.findByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(List.of(new TaskSchedule()));
        
        TaskSchedule result = preemptionResourceService.preemptResource(
                taskInfo, equipment, startTime, endTime);
        
        assertNull(result);
        verify(taskScheduleMapper).findByEquipmentAndTimeRange(
                equipment.getEquipmentId(),
                startTime,
                endTime
        );
    }
    
    @Test
    void preemptResource_WhenEquipmentAvailable_ShouldCreateSchedule() {
        // 模拟设备可用
        when(taskScheduleMapper.findByEquipmentAndTimeRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        TaskSchedule result = preemptionResourceService.preemptResource(
                taskInfo, equipment, startTime, endTime);
        
        assertNotNull(result);
        assertEquals(taskInfo.getTaskId(), result.getTaskId());
        assertEquals(equipment.getGroundStationId(), result.getGroundStationId());
        assertEquals(equipment.getEquipmentId(), result.getEquipmentId());
        assertEquals(startTime, result.getStartTime());
        assertEquals(endTime, result.getEndTime());
        
        verify(taskScheduleMapper).insert(result);
    }
} 