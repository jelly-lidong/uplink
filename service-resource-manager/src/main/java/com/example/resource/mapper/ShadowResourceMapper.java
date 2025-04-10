package com.example.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.model.entity.task.ShadowRelation;
import com.common.model.entity.task.TaskSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 影随资源数据访问接口
 */
@Mapper
public interface ShadowResourceMapper extends BaseMapper<ShadowRelation> {
    
    /**
     * 查询指定时间范围内的任务调度计划
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务调度计划列表
     */
    @Select("SELECT * FROM task_schedule WHERE " +
            "((start_time <= #{endTime} AND end_time >= #{startTime}) OR " +
            "(start_time <= #{endTime} AND start_time >= #{startTime}) OR " +
            "(end_time <= #{endTime} AND end_time >= #{startTime}))")
    List<TaskSchedule> getSchedulesInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询指定设备在指定时间范围内的任务调度计划
     * 
     * @param equipmentId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务调度计划列表
     */
    @Select("SELECT * FROM task_schedule WHERE equipment_id = #{equipmentId} AND " +
            "((start_time <= #{endTime} AND end_time >= #{startTime}) OR " +
            "(start_time <= #{endTime} AND start_time >= #{startTime}) OR " +
            "(end_time <= #{endTime} AND end_time >= #{startTime}))")
    List<TaskSchedule> getEquipmentSchedules(@Param("equipmentId") Long equipmentId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据主任务ID查询影随关系
     * 
     * @param primaryTaskId 主任务ID
     * @return 影随关系列表
     */
    @Select("SELECT sr.* FROM shadow_relation sr " +
            "JOIN task_schedule ts1 ON sr.primary_schedule_id = ts1.id " +
            "WHERE ts1.task_id = #{primaryTaskId}")
    List<ShadowRelation> getShadowRelationsByPrimaryTaskId(@Param("primaryTaskId") Long primaryTaskId);
    
    /**
     * 根据影随任务ID查询影随关系
     * 
     * @param shadowTaskId 影随任务ID
     * @return 影随关系列表
     */
    @Select("SELECT sr.* FROM shadow_relation sr " +
            "JOIN task_schedule ts2 ON sr.shadow_schedule_id = ts2.id " +
            "WHERE ts2.task_id = #{shadowTaskId}")
    List<ShadowRelation> getShadowRelationsByShadowTaskId(@Param("shadowTaskId") Long shadowTaskId);
} 