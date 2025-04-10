package com.example.resource.controller;

import com.common.model.entity.task.TaskInfo;
import com.common.model.response.Result;
import com.example.resource.service.UnifiedSchedulingService;
import com.example.resource.service.strategy.ResourceApplicationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 一体化资源申请控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/resource/application")
@RequiredArgsConstructor
public class ResourceApplicationController {

    private final UnifiedSchedulingService schedulingService;

    /**
     * 申请资源
     *
     * @param taskInfo 任务信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 资源申请结果
     */
    @PostMapping("/apply")
    public Result<ResourceApplicationResult> applyResource(
            @RequestBody TaskInfo taskInfo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            ResourceApplicationResult result = schedulingService.schedule(taskInfo, startTime, endTime);
            if (result.isSuccess()) {
                return Result.success(result);
            }
            return Result.failed(result.getMessage());
        } catch (Exception e) {
            log.error("资源申请异常", e);
            return Result.failed("资源申请过程中发生异常: " + e.getMessage());
        }
    }
} 