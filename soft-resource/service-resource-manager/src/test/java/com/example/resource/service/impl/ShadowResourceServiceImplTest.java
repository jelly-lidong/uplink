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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShadowResourceServiceImplTest {

    @Mock
    private GroundEquipmentMapper groundEquipmentMapper;

    @Mock
    private TaskInfoMapper taskInfoMapper;

    @Mock
    private TaskScheduleMapper taskScheduleMapper;

    @InjectMocks
    private ShadowResourceServiceImpl shadowResourceService;

    private TaskInfo task;
    private GroundEquipment equipment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        task = new TaskInfo();
        task.setId(1L);
        task.setTaskType("测控");

        equipment = new GroundEquipment();
        equipment.setId(1L);
        equipment.setGroundStationId(1L);
        equipment.setSupportShadowMode(true);

        startTime = LocalDateTime.now();
        endTime = startTime.plusHours(1);
    }

    @Test
    void findShadowResource_WhenNoEquipmentAvailable_ShouldReturnNull() {
        // 准备测试数据
        when(groundEquipmentMapper.selectByTaskType(any())).thenReturn(new ArrayList<>());

        // 执行测试
        GroundEquipment result = shadowResourceService.findShadowResource(task, startTime, endTime);

        // 验证结果
        assertNull(result);
        verify(groundEquipmentMapper).selectByTaskType(task.getTaskType());
    }

    @Test
    void findShadowResource_WhenEquipmentAvailable_ShouldReturnEquipment() {
        // 准备测试数据
        List<GroundEquipment> equipments = new ArrayList<>();
        equipments.add(equipment);
        when(groundEquipmentMapper.selectByTaskType(any())).thenReturn(equipments);
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());

        // 执行测试
        GroundEquipment result = shadowResourceService.findShadowResource(task, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(equipment.getId(), result.getId());
        verify(groundEquipmentMapper).selectByTaskType(task.getTaskType());
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment.getId(), startTime, endTime);
    }

    @Test
    void findShadowResource_WhenMultipleEquipmentsAvailable_ShouldReturnFirstAvailable() {
        // 准备测试数据
        GroundEquipment equipment2 = new GroundEquipment();
        equipment2.setId(2L);
        equipment2.setGroundStationId(1L);
        equipment2.setSupportShadowMode(true);

        List<GroundEquipment> equipments = new ArrayList<>();
        equipments.add(equipment);
        equipments.add(equipment2);

        when(groundEquipmentMapper.selectByTaskType(any())).thenReturn(equipments);
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());

        // 执行测试
        GroundEquipment result = shadowResourceService.findShadowResource(task, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(equipment.getId(), result.getId());
        verify(groundEquipmentMapper).selectByTaskType(task.getTaskType());
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment.getId(), startTime, endTime);
    }

    @Test
    void findShadowResource_WhenEquipmentHasOverlappingSchedule_ShouldSkipAndFindNext() {
        // 准备测试数据
        GroundEquipment equipment2 = new GroundEquipment();
        equipment2.setId(2L);
        equipment2.setGroundStationId(1L);
        equipment2.setSupportShadowMode(true);

        List<GroundEquipment> equipments = new ArrayList<>();
        equipments.add(equipment);
        equipments.add(equipment2);

        List<TaskSchedule> overlappingSchedules = new ArrayList<>();
        TaskSchedule schedule = new TaskSchedule();
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        overlappingSchedules.add(schedule);

        when(groundEquipmentMapper.selectByTaskType(any())).thenReturn(equipments);
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(eq(equipment.getId()), any(), any()))
                .thenReturn(overlappingSchedules);
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(eq(equipment2.getId()), any(), any()))
                .thenReturn(new ArrayList<>());

        // 执行测试
        GroundEquipment result = shadowResourceService.findShadowResource(task, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(equipment2.getId(), result.getId());
        verify(groundEquipmentMapper).selectByTaskType(task.getTaskType());
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment.getId(), startTime, endTime);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment2.getId(), startTime, endTime);
    }

    @Test
    void allocateShadowResource_WhenEquipmentNotAvailable_ShouldReturnNull() {
        // 准备测试数据
        equipment.setSupportShadowMode(false);

        // 执行测试
        TaskSchedule result = shadowResourceService.allocateShadowResource(task, equipment, startTime, endTime);

        // 验证结果
        assertNull(result);
        verify(taskScheduleMapper, never()).insert(any());
    }

    @Test
    void allocateShadowResource_WhenEquipmentAvailable_ShouldCreateSchedule() {
        // 准备测试数据
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(taskScheduleMapper.insert(any())).thenReturn(1);

        // 执行测试
        TaskSchedule result = shadowResourceService.allocateShadowResource(task, equipment, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(task.getId(), result.getTaskId());
        assertEquals(equipment.getId(), result.getEquipmentId());
        assertEquals(equipment.getGroundStationId(), result.getGroundStationId());
        assertEquals(startTime, result.getStartTime());
        assertEquals(endTime, result.getEndTime());
        verify(taskScheduleMapper).insert(any());
    }

    @Test
    void allocateShadowResource_WhenScheduleInsertFails_ShouldReturnNull() {
        // 准备测试数据
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(taskScheduleMapper.insert(any())).thenReturn(0);

        // 执行测试
        TaskSchedule result = shadowResourceService.allocateShadowResource(task, equipment, startTime, endTime);

        // 验证结果
        assertNull(result);
        verify(taskScheduleMapper).insert(any());
    }

    @Test
    void isSupportShadowMode_WhenEquipmentSupportsShadowMode_ShouldReturnTrue() {
        // 执行测试
        boolean result = shadowResourceService.isSupportShadowMode(equipment);

        // 验证结果
        assertTrue(result);
    }

    @Test
    void isSupportShadowMode_WhenEquipmentNotSupportsShadowMode_ShouldReturnFalse() {
        // 准备测试数据
        equipment.setSupportShadowMode(false);

        // 执行测试
        boolean result = shadowResourceService.isSupportShadowMode(equipment);

        // 验证结果
        assertFalse(result);
    }

    @Test
    void isShadowResourceAvailable_WhenNoOverlappingSchedules_ShouldReturnTrue() {
        // 准备测试数据
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());

        // 执行测试
        boolean result = shadowResourceService.isShadowResourceAvailable(equipment, startTime, endTime);

        // 验证结果
        assertTrue(result);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment.getId(), startTime, endTime);
    }

    @Test
    void isShadowResourceAvailable_WhenHasOverlappingSchedules_ShouldReturnFalse() {
        // 准备测试数据
        List<TaskSchedule> schedules = new ArrayList<>();
        TaskSchedule schedule = new TaskSchedule();
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedules.add(schedule);
        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(schedules);

        // 执行测试
        boolean result = shadowResourceService.isShadowResourceAvailable(equipment, startTime, endTime);

        // 验证结果
        assertFalse(result);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment.getId(), startTime, endTime);
    }

    @Test
    void isShadowResourceAvailable_WhenSchedulePartiallyOverlaps_ShouldReturnFalse() {
        // 准备测试数据
        List<TaskSchedule> schedules = new ArrayList<>();
        TaskSchedule schedule = new TaskSchedule();
        schedule.setStartTime(startTime.minusMinutes(30));
        schedule.setEndTime(startTime.plusMinutes(30));
        schedules.add(schedule);

        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(schedules);

        // 执行测试
        boolean result = shadowResourceService.isShadowResourceAvailable(equipment, startTime, endTime);

        // 验证结果
        assertFalse(result);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment.getId(), startTime, endTime);
    }

    @Test
    void isShadowResourceAvailable_WhenScheduleContainsTimeRange_ShouldReturnFalse() {
        // 准备测试数据
        List<TaskSchedule> schedules = new ArrayList<>();
        TaskSchedule schedule = new TaskSchedule();
        schedule.setStartTime(startTime.minusMinutes(30));
        schedule.setEndTime(endTime.plusMinutes(30));
        schedules.add(schedule);

        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(schedules);

        // 执行测试
        boolean result = shadowResourceService.isShadowResourceAvailable(equipment, startTime, endTime);

        // 验证结果
        assertFalse(result);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment.getId(), startTime, endTime);
    }

    @Test
    void isShadowResourceAvailable_WhenTimeRangeContainsSchedule_ShouldReturnFalse() {
        // 准备测试数据
        List<TaskSchedule> schedules = new ArrayList<>();
        TaskSchedule schedule = new TaskSchedule();
        schedule.setStartTime(startTime.plusMinutes(15));
        schedule.setEndTime(endTime.minusMinutes(15));
        schedules.add(schedule);

        when(taskScheduleMapper.selectByEquipmentAndTimeRange(anyLong(), any(), any()))
                .thenReturn(schedules);

        // 执行测试
        boolean result = shadowResourceService.isShadowResourceAvailable(equipment, startTime, endTime);

        // 验证结果
        assertFalse(result);
        verify(taskScheduleMapper).selectByEquipmentAndTimeRange(equipment.getId(), startTime, endTime);
    }
} 