package com.example.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.model.entity.task.TaskInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {
} 