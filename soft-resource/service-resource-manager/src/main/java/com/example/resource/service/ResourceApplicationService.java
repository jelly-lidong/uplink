package com.example.resource.service;

import com.common.model.entity.resource.GroundEquipment;
import com.common.model.entity.task.TaskInfo;
import com.common.model.entity.task.TaskSchedule;
import com.example.resource.service.strategy.ResourceApplicationResult;
import com.example.resource.service.strategy.ResourceApplicationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 一体化资源申请服务接口
 * 按照优先级顺序依次尝试四种资源申请方式
 */
@Slf4j
@Service
@RequiredArgsConstructor
public interface ResourceApplicationService {
    
    private final List<ResourceApplicationStrategy> strategies;
    
    /**
     * 申请资源
     * 按照优先级顺序依次尝试：
     * 1. 影随资源
     * 2. 空闲资源
     * 3. 可转让资源
     * 4. 可抢占资源
     *
     * @param taskInfo 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 资源申请结果
     */
    ResourceApplicationResult applyResource(TaskInfo taskInfo, LocalDateTime startTime, LocalDateTime endTime);
}