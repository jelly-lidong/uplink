-- 任务信息表
CREATE TABLE `task_info`
(
    `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id`              varchar(64)  DEFAULT NULL COMMENT '任务ID',
    `task_name`            varchar(255) DEFAULT NULL COMMENT '任务名称',
    `task_desc`            varchar(512) DEFAULT NULL COMMENT '任务描述',
    `priority_level`       tinyint      DEFAULT NULL COMMENT '优先级别：1-低，2-中，3-高',
    `importance_level`     tinyint      DEFAULT NULL COMMENT '重要程度：1-低，2-中，3-高',
    `task_mode_id`         varchar(64)  DEFAULT NULL COMMENT '任务模式',
    `task_type`            varchar(64)  DEFAULT NULL COMMENT '任务类型',
    `task_status`          varchar(32)  DEFAULT NULL COMMENT '任务状态',
    `parent_task_id`       varchar(64)  DEFAULT NULL COMMENT '父任务ID',
    `start_time`           datetime     DEFAULT NULL COMMENT '开始时间',
    `end_time`             datetime     DEFAULT NULL COMMENT '结束时间',
    `task_satellite_ids`   json         DEFAULT NULL COMMENT '任务卫星ID集合',
    `target_satellite_ids` json         DEFAULT NULL COMMENT '目标卫星ID集合',
    `create_time`          datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`          datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`) COMMENT '任务ID索引',
    KEY `idx_parent_task_id` (`parent_task_id`) COMMENT '父任务ID索引',
    KEY `idx_task_status` (`task_status`) COMMENT '任务状态索引',
    KEY `idx_start_time` (`start_time`) COMMENT '开始时间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='任务信息';


-- 任务模板表
CREATE TABLE `task_template`
(
    `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `template_id`          varchar(64)  DEFAULT NULL COMMENT '模板ID',
    `template_name`        varchar(255) DEFAULT NULL COMMENT '模板名称',
    `template_desc`        varchar(512) DEFAULT NULL COMMENT '模板描述',
    `is_enabled`           boolean      DEFAULT NULL COMMENT '是否启用',
    `task_modes`           json         DEFAULT NULL COMMENT '任务模式',
    `task_satellite_ids`   json         DEFAULT NULL COMMENT '任务卫星ID集合',
    `target_satellite_ids` json         DEFAULT NULL COMMENT '目标卫星ID集合',
    `create_time`          datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`          datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='任务模板';

-- 任务模式配置表
CREATE TABLE `task_mode`
(
    `id`               bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_template_id` bigint       DEFAULT NULL COMMENT '任务模板ID',
    `mode_name`        varchar(255) DEFAULT NULL COMMENT '模式名称',
    `mode_type`        varchar(32)  DEFAULT NULL COMMENT '模式类型：SCHEDULED-定时执行，PERIODIC-周期执行，CONDITIONAL-条件触发',
    `execution_time`   varchar(32)  DEFAULT NULL COMMENT '执行时间，格式：HH:mm',
    `period`           int          DEFAULT NULL COMMENT '执行周期(分钟)',
    `task_condition`   varchar(512) DEFAULT NULL COMMENT '触发条件',
    `create_time`      datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`      datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_template_id` (`task_template_id`) COMMENT '任务模板ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='任务模式配置';



CREATE TABLE `t_constraint`
(
    `id`              bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type_id`         bigint       DEFAULT NULL COMMENT '约束类型ID',
    `name`            varchar(255) DEFAULT NULL COMMENT '约束名称',
    `constraint_type` varchar(64)  DEFAULT NULL COMMENT '约束类型',
    `description`     varchar(512) DEFAULT NULL COMMENT '约束描述',
    `min_value`       double       DEFAULT NULL COMMENT '约束最小值',
    `max_value`       double       DEFAULT NULL COMMENT '约束最大值',
    `data_type`       tinyint      DEFAULT NULL COMMENT '约束数据类型：1-数值，2-字符串，3-布尔，4-日期',
    `required`        tinyint      DEFAULT NULL COMMENT '是否必填：0-否，1-是',
    `sort_no`         int          DEFAULT NULL COMMENT '排序号',
    `create_time`     datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type_id` (`type_id`) COMMENT '约束类型ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='约束';

CREATE TABLE `task_template_constraint`
(
    `id`              bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_template_id`         bigint       DEFAULT NULL COMMENT '任务模板ID',
    `constraint_type` varchar(32)  DEFAULT NULL COMMENT '约束类型：TIME-时间约束，RESOURCE-资源约束，VISIBILITY-可见性约束，EQUIPMENT-设备约束，PRIORITY-优先级约束，ORBITAL-轨道动力学约束，DEPENDENCY-任务依赖约束',
    `name`            varchar(255) DEFAULT NULL COMMENT '约束名称',
    `description`     varchar(512) DEFAULT NULL COMMENT '约束描述',
    `min_value`       double       DEFAULT NULL COMMENT '约束最小值',
    `max_value`       double       DEFAULT NULL COMMENT '约束最大值',
    `data_type`       tinyint      DEFAULT NULL COMMENT '约束数据类型：1-数值，2-字符串，3-布尔，4-日期',
    `is_enabled`      tinyint(1)   DEFAULT NULL COMMENT '是否启用：0-禁用，1-启用',
    `required`        tinyint      DEFAULT NULL COMMENT '是否必填：0-否，1-是',
    `sort_no`         int          DEFAULT NULL COMMENT '排序号',
    `create_time`     datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_template_id` (`task_template_id`) COMMENT '任务模板ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='任务约束';

CREATE TABLE `task_constraint`
(
    `id`              bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id`         bigint       DEFAULT NULL COMMENT '任务ID',
    `constraint_type` varchar(32)  DEFAULT NULL COMMENT '约束类型：TIME-时间约束，RESOURCE-资源约束，VISIBILITY-可见性约束，EQUIPMENT-设备约束，PRIORITY-优先级约束，ORBITAL-轨道动力学约束，DEPENDENCY-任务依赖约束',
    `name`            varchar(255) DEFAULT NULL COMMENT '约束名称',
    `description`     varchar(512) DEFAULT NULL COMMENT '约束描述',
    `min_value`       double       DEFAULT NULL COMMENT '约束最小值',
    `max_value`       double       DEFAULT NULL COMMENT '约束最大值',
    `data_type`       tinyint      DEFAULT NULL COMMENT '约束数据类型：1-数值，2-字符串，3-布尔，4-日期',
    `is_enabled`      tinyint(1)   DEFAULT NULL COMMENT '是否启用：0-禁用，1-启用',
    `required`        tinyint      DEFAULT NULL COMMENT '是否必填：0-否，1-是',
    `sort_no`         int          DEFAULT NULL COMMENT '排序号',
    `create_time`     datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`) COMMENT '任务ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='任务约束';