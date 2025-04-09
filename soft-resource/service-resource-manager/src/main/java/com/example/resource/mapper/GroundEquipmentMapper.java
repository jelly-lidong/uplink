package com.example.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.model.entity.resource.GroundEquipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地面站装备Mapper接口
 */
@Mapper
public interface GroundEquipmentMapper extends BaseMapper<GroundEquipment> {
    
    /**
     * 根据任务类型查询支持该类型的设备
     *
     * @param taskType 任务类型
     * @return 支持该任务类型的设备列表
     */
    @Select("SELECT * FROM t_ground_equipment WHERE JSON_CONTAINS(supported_task_types, #{taskType})")
    List<GroundEquipment> selectByTaskType(@Param("taskType") String taskType);
} 