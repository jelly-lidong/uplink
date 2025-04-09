package com.example.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务信息Mapper接口
 */
@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {
    
    /**
     * 查找可抢占的设备
     *
     * @param taskType 任务类型
     * @param priorityLevel 优先级等级
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可抢占的设备列表
     */
    @Select("SELECT e.* FROM ground_equipment e " +
            "JOIN task_schedule ts ON e.id = ts.equipment_id " +
            "JOIN task_info ti ON ts.task_id = ti.id " +
            "WHERE e.task_type = #{taskType} " +
            "AND ti.priority_level < #{priorityLevel} " +
            "AND ts.start_time < #{endTime} " +
            "AND ts.end_time > #{startTime}")
    List<GroundEquipment> findPreemptibleEquipments(
            @Param("taskType") String taskType,
            @Param("priorityLevel") Integer priorityLevel,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找可转让的设备
     *
     * @param taskType 任务类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可转让的设备列表
     */
    @Select("SELECT e.* FROM ground_equipment e " +
            "LEFT JOIN task_schedule ts ON e.id = ts.equipment_id " +
            "AND ts.start_time < #{endTime} " +
            "AND ts.end_time > #{startTime} " +
            "WHERE e.task_type = #{taskType} " +
            "AND ts.id IS NULL")
    List<GroundEquipment> findTransferableEquipments(
            @Param("taskType") String taskType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找空闲的设备
     *
     * @param taskType 任务类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 空闲的设备列表
     */
    @Select("SELECT e.* FROM ground_equipment e " +
            "LEFT JOIN task_schedule ts ON e.id = ts.equipment_id " +
            "AND ts.start_time < #{endTime} " +
            "AND ts.end_time > #{startTime} " +
            "WHERE e.task_type = #{taskType} " +
            "AND ts.id IS NULL " +
            "AND e.status = 'IDLE'")
    List<GroundEquipment> findIdleEquipments(
            @Param("taskType") String taskType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找支持影子模式的设备
     *
     * @param taskType 任务类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 支持影子模式的设备列表
     */
    @Select("SELECT e.* FROM ground_equipment e " +
            "WHERE e.task_type = #{taskType} " +
            "AND e.support_shadow_mode = true")
    List<GroundEquipment> findShadowModeEquipments(
            @Param("taskType") String taskType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
} 