package com.example.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.model.entity.task.TaskSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务调度计划数据访问接口
 */
@Mapper
public interface TaskScheduleMapper extends BaseMapper<TaskSchedule> {
    
    /**
     * 根据任务ID和设备ID查询调度计划
     * 
     * @param taskId 任务ID
     * @param equipmentId 设备ID
     * @return 调度计划
     */
    @Select("SELECT * FROM task_schedule WHERE task_id = #{taskId} AND equipment_id = #{equipmentId} LIMIT 1")
    TaskSchedule getByTaskIdAndEquipmentId(@Param("taskId") Long taskId, @Param("equipmentId") Long equipmentId);
    
    /**
     * 根据任务ID查询调度计划列表
     * 
     * @param taskId 任务ID
     * @return 调度计划列表
     */
    @Select("SELECT * FROM task_schedule WHERE task_id = #{taskId}")
    List<TaskSchedule> getByTaskId(@Param("taskId") Long taskId);
    
    /**
     * 查询具有影随关系的调度计划
     * 
     * @return 调度计划列表
     */
    @Select("SELECT * FROM task_schedule WHERE has_shadow_relation = true")
    List<TaskSchedule> getWithShadowRelation();

    /**
     * 根据设备和时间范围查询任务调度
     *
     * @param equipmentId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务调度列表
     */
    @Select("SELECT * FROM t_task_schedule " +
            "WHERE equipment_id = #{equipmentId} " +
            "AND ((start_time BETWEEN #{startTime} AND #{endTime}) " +
            "OR (end_time BETWEEN #{startTime} AND #{endTime}) " +
            "OR (start_time <= #{startTime} AND end_time >= #{endTime})) " +
            "ORDER BY start_time")
    List<TaskSchedule> selectByEquipmentAndTimeRange(
            @Param("equipmentId") Long equipmentId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
} 