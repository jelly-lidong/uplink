-- 任务调度计划表
CREATE TABLE IF NOT EXISTS `task_schedule` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `ground_station_id` bigint NOT NULL COMMENT '地面站ID',
    `equipment_id` bigint NOT NULL COMMENT '设备ID',
    `start_time` datetime NOT NULL COMMENT '计划开始时间',
    `end_time` datetime NOT NULL COMMENT '计划结束时间',
    `status` varchar(32) NOT NULL COMMENT '计划状态',
    `has_shadow_relation` tinyint(1) DEFAULT 0 COMMENT '是否有影随关系',
    `description` varchar(512) DEFAULT NULL COMMENT '描述信息',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_equipment_id` (`equipment_id`),
    KEY `idx_time_range` (`start_time`, `end_time`),
    KEY `idx_has_shadow` (`has_shadow_relation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务调度计划';

-- 影随关系表
CREATE TABLE IF NOT EXISTS `shadow_relation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `primary_schedule_id` bigint NOT NULL COMMENT '主任务调度ID',
    `shadow_schedule_id` bigint NOT NULL COMMENT '影随任务调度ID',
    `shadow_mode` tinyint(1) NOT NULL COMMENT '影随模式: 0-不支持, 1-测控, 2-数传, 3-一体化',
    `priority` int DEFAULT 1 COMMENT '优先级',
    `resource_occupancy` double DEFAULT 0.5 COMMENT '资源占用率(%)',
    `splittable` tinyint(1) DEFAULT 0 COMMENT '是否可拆分',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary_schedule` (`primary_schedule_id`),
    KEY `idx_shadow_schedule` (`shadow_schedule_id`),
    CONSTRAINT `fk_primary_schedule` FOREIGN KEY (`primary_schedule_id`) REFERENCES `task_schedule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_shadow_schedule` FOREIGN KEY (`shadow_schedule_id`) REFERENCES `task_schedule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='影随关系';

-- 设备能力表
CREATE TABLE IF NOT EXISTS `equipment_capability` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `equipment_id` bigint NOT NULL COMMENT '设备ID',
    `equipment_name` varchar(255) DEFAULT NULL COMMENT '设备名称',
    `shadow_mode` tinyint(1) DEFAULT 0 COMMENT '影随模式: 0-不支持, 1-测控, 2-数传, 3-一体化',
    `max_concurrent_tasks` int DEFAULT 1 COMMENT '最大同时任务数',
    `current_task_count` int DEFAULT 0 COMMENT '当前任务数',
    `supported_task_types` json DEFAULT NULL COMMENT '支持的任务类型',
    `load_threshold` double DEFAULT 80.0 COMMENT '资源负载阈值(%)',
    `current_load` double DEFAULT 0.0 COMMENT '当前负载(%)',
    `status` varchar(32) DEFAULT 'ACTIVE' COMMENT '状态',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_equipment_id` (`equipment_id`),
    CONSTRAINT `fk_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `t_ground_equipment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备能力';

-- 为地面站设备表添加影随相关字段
ALTER TABLE `t_ground_equipment` 
ADD COLUMN `support_shadow_mode` tinyint(1) DEFAULT 0 COMMENT '是否支持测控数传一体化' AFTER `status`,
ADD COLUMN `supported_task_types` json DEFAULT NULL COMMENT '支持的任务类型' AFTER `support_shadow_mode`,
ADD COLUMN `max_parallel_tasks` int DEFAULT 1 COMMENT '最大并行任务数' AFTER `supported_task_types`,
ADD COLUMN `current_parallel_tasks` int DEFAULT 0 COMMENT '当前并行任务数' AFTER `max_parallel_tasks`,
ADD COLUMN `capabilities` json DEFAULT NULL COMMENT '设备能力列表' AFTER `current_parallel_tasks`; 