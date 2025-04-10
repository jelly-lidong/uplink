create table t_constraint
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    type_id         bigint       null comment '约束类型ID',
    name            varchar(255) null comment '约束名称',
    constraint_type varchar(64)  null comment '约束类型',
    description     varchar(512) null comment '约束描述',
    min_value       double       null comment '约束最小值',
    max_value       double       null comment '约束最大值',
    data_type       tinyint      null comment '约束数据类型：1-数值，2-字符串，3-布尔，4-日期',
    required        tinyint      null comment '是否必填：0-否，1-是',
    sort_no         int          null comment '排序号',
    create_time     datetime     null comment '创建时间',
    update_time     datetime     null comment '更新时间',
    is_enabled      tinyint(1)   null
)
    comment '约束';

create index idx_type_id
    on t_constraint (type_id)
    comment '约束类型ID索引';

create table t_ground_station
(
    id           bigint auto_increment comment '地面站ID'
        primary key,
    station_name varchar(100)                       not null comment '地面站名称',
    station_code varchar(50)                        null comment '地面站编号',
    station_type varchar(50)                        null comment '地面站类型',
    location     varchar(200)                       null comment '地理位置',
    latitude     double                             null comment '纬度',
    longitude    double                             null comment '经度',
    altitude     double                             null comment '海拔高度(米)',
    status       varchar(20)                        null comment '站点状态',
    description  text                               null comment '描述信息',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint station_code
        unique (station_code)
)
    comment '地面站信息';

create table t_ground_equipment
(
    id                bigint auto_increment comment '装备ID'
        primary key,
    ground_station_id bigint                             not null comment '所属地面站ID',
    equipment_name    varchar(100)                       not null comment '装备名称',
    equipment_code    varchar(50)                        null comment '装备编号',
    equipment_type    varchar(50)                        null comment '装备类型',
    min_frequency     double                             null comment '最小工作频率(MHz)',
    max_frequency     double                             null comment '最大工作频率(MHz)',
    frequency_band    varchar(20)                        null comment '频段',
    polarization      varchar(50)                        null comment '极化方式',
    gain              double                             null comment '天线增益(dB)',
    status            varchar(20)                        null comment '装备状态',
    description       text                               null comment '描述信息',
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint equipment_code
        unique (equipment_code),
    constraint t_ground_equipment_ibfk_1
        foreign key (ground_station_id) references t_ground_station (id)
)
    comment '地面站装备信息';

create index idx_equipment_code
    on t_ground_equipment (equipment_code);

create index idx_ground_station_id
    on t_ground_equipment (ground_station_id);

create index idx_status
    on t_ground_equipment (status);

create index idx_station_code
    on t_ground_station (station_code);

create index idx_status
    on t_ground_station (status);

create table t_satellite
(
    id                  bigint auto_increment comment '卫星ID' primary key,
    satellite_name_cn   varchar(100)                       not null comment '卫星中文名称',
    satellite_name_en   varchar(100)                       not null comment '卫星英文名称',
    satellite_code      varchar(50)                        null comment '卫星编号',
    cospar_id           varchar(20)                        null comment '国际编号(COSPAR ID)',
    norad_id            varchar(20)                        null comment 'NORAD编号',
    orbit_type          varchar(50)                        null comment '轨道类型',
    semi_major_axis     double                             null comment '半长轴(千米)',
    eccentricity        double                             null comment '偏心率',
    inclination         double                             null comment '轨道倾角(度)',
    right_ascension     double                             null comment '升交点赤经(度)',
    argument_of_perigee double                             null comment '近地点幅角(度)',
    mean_anomaly        double                             null comment '平近点角(度)',
    epoch_time          datetime                           null comment '历元时间',
    tle_line1           varchar(100)                       null comment '两行根数集第一行',
    tle_line2           varchar(100)                       null comment '两行根数集第二行',
    launch_time         datetime                           null comment '发射时间',
    status              varchar(20)                        null comment '卫星状态',
    description         text                               null comment '描述信息',
    tags                json                               null comment '标签列表',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint satellite_code
        unique (satellite_code)
)
    comment '卫星信息';

create table t_satellite_group
(
    id                  bigint auto_increment comment '卫星ID' primary key,
    group_name   varchar(100)                       not null comment '卫星组名称',
    group_desc   varchar(100)                       null comment '卫星组描述',
    group_status      varchar(50)                        null comment '卫星组状态',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint group_name
        unique (group_name)
)
    comment '卫星信息';


create table t_payload
(
    id           bigint auto_increment comment '载荷ID'
        primary key,
    satellite_id bigint                             not null comment '所属卫星ID',
    payload_name varchar(100)                       not null comment '载荷名称',
    payload_type varchar(50)                        null comment '载荷类型',
    payload_code varchar(50)                        null comment '载荷编号',
    status       varchar(20)                        null comment '载荷状态',
    description  text                               null comment '描述信息',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint payload_code
        unique (payload_code),
    constraint t_payload_ibfk_1
        foreign key (satellite_id) references t_satellite (id)
)
    comment '卫星载荷信息';

create index idx_payload_code
    on t_payload (payload_code);

create index idx_satellite_id
    on t_payload (satellite_id);

create index idx_satellite_code
    on t_satellite (satellite_code);

create index idx_status
    on t_satellite (status);

create table task_comment
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    task_id         varchar(64)                        not null comment '任务ID',
    comment_content text                               not null comment '评论内容',
    creator_id      varchar(64)                        not null comment '评论人ID',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '任务评论表';

create index idx_task_id
    on task_comment (task_id);

create table task_constraint
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    task_id         bigint       null comment '任务模板ID',
    constraint_type varchar(32)  null comment '约束类型：TIME-时间约束，RESOURCE-资源约束，VISIBILITY-可见性约束，EQUIPMENT-设备约束，PRIORITY-优先级约束，ORBITAL-轨道动力学约束，DEPENDENCY-任务依赖约束',
    name            varchar(255) null comment '约束名称',
    description     varchar(512) null comment '约束描述',
    min_value       double       null comment '约束最小值',
    max_value       double       null comment '约束最大值',
    data_type       tinyint      null comment '约束数据类型：1-数值，2-字符串，3-布尔，4-日期',
    is_enabled      tinyint(1)   null comment '是否启用：0-禁用，1-启用',
    required        tinyint      null comment '是否必填：0-否，1-是',
    sort_no         int          null comment '排序号',
    create_time     datetime     null comment '创建时间',
    update_time     datetime     null comment '更新时间'
)
    comment '任务约束';

create index idx_task_id
    on task_constraint (task_id)
    comment '任务ID索引';

create table task_info
(
    id                   bigint auto_increment comment '主键ID'
        primary key,
    task_id              varchar(64)  null comment '任务ID',
    task_name            varchar(255) null comment '任务名称',
    task_desc            varchar(512) null comment '任务描述',
    priority_level       tinyint      null comment '优先级别：1-低，2-中，3-高',
    importance_level     tinyint      null comment '重要程度：1-低，2-中，3-高',
    task_mode_id         varchar(64)  null comment '任务模式',
    task_type            varchar(64)  null comment '任务类型',
    task_status          varchar(32)  null comment '任务状态',
    parent_task_id       varchar(64)  null comment '父任务ID',
    start_time           datetime     null comment '开始时间',
    end_time             datetime     null comment '结束时间',
    task_satellite_ids   json         null comment '任务卫星ID集合',
    target_satellite_ids json         null comment '目标卫星ID集合',
    create_time          datetime     null comment '创建时间',
    update_time          datetime     null comment '更新时间',
    ground_station_ids   json         null
)
    comment '任务信息';

create index idx_parent_task_id
    on task_info (parent_task_id)
    comment '父任务ID索引';

create index idx_start_time
    on task_info (start_time)
    comment '开始时间索引';

create index idx_task_id
    on task_info (task_id)
    comment '任务ID索引';

create index idx_task_status
    on task_info (task_status)
    comment '任务状态索引';

create table task_mode
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    task_template_id bigint       null comment '任务模板ID',
    mode_name        varchar(255) null comment '模式名称',
    mode_type        varchar(32)  null comment '模式类型：SCHEDULED-定时执行，PERIODIC-周期执行，CONDITIONAL-条件触发',
    execution_time   varchar(32)  null comment '执行时间，格式：HH:mm',
    period int null comment '执行周期(分钟)',
    task_condition   varchar(512) null comment '触发条件',
    create_time      datetime     null comment '创建时间',
    update_time      datetime     null comment '更新时间'
)
    comment '任务模式配置';

create index idx_task_template_id
    on task_mode (task_template_id)
    comment '任务模板ID索引';

create table task_template
(
    id                   bigint auto_increment comment '主键ID'
        primary key,
    template_id          varchar(64)  null comment '模板ID',
    template_name        varchar(255) null comment '模板名称',
    template_desc        varchar(512) null comment '模板描述',
    is_enabled           tinyint(1)   null comment '是否启用',
    task_modes           json         null comment '任务模式',
    task_satellite_ids   json         null comment '任务卫星ID集合',
    target_satellite_ids json         null comment '目标卫星ID集合',
    create_time          datetime     null comment '创建时间',
    update_time          datetime     null comment '更新时间',
    task_constraint_ids  json         null,
    ground_station_ids   json         null
)
    comment '任务模板';

create table task_template_constraint
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    task_template_id bigint       null comment '任务模板ID',
    constraint_type  varchar(32)  null comment '约束类型：TIME-时间约束，RESOURCE-资源约束，VISIBILITY-可见性约束，EQUIPMENT-设备约束，PRIORITY-优先级约束，ORBITAL-轨道动力学约束，DEPENDENCY-任务依赖约束',
    name             varchar(255) null comment '约束名称',
    description      varchar(512) null comment '约束描述',
    min_value        double       null comment '约束最小值',
    max_value        double       null comment '约束最大值',
    data_type        tinyint      null comment '约束数据类型：1-数值，2-字符串，3-布尔，4-日期',
    is_enabled       tinyint(1)   null comment '是否启用：0-禁用，1-启用',
    required         tinyint      null comment '是否必填：0-否，1-是',
    sort_no          int          null comment '排序号',
    create_time      datetime     null comment '创建时间',
    update_time      datetime     null comment '更新时间'
)
    comment '任务约束';

create index idx_task_template_id
    on task_template_constraint (task_template_id)
    comment '任务模板ID索引';

create table task_schedule
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    task_id            bigint                             not null comment '任务ID',
    ground_station_id  bigint                             null comment '地面站ID',
    equipment_id       bigint                             null comment '设备ID',
    start_time         datetime                           null comment '计划开始时间',
    end_time           datetime                           null comment '计划结束时间',
    status             varchar(32)                        null comment '计划状态',
    has_shadow_relation tinyint(1)                        null comment '是否有影随关系：0-否，1-是',
    is_preempted       tinyint(1)                         null comment '是否被抢占：0-否，1-是',
    description        varchar(512)                       null comment '描述信息',
    create_time        datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time        datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_task_schedule_task
        foreign key (task_id) references task_info (id)
)
    comment '任务调度计划';

create index idx_task_id_schedule
    on task_schedule (task_id);

create index idx_ground_station_id_schedule
    on task_schedule (ground_station_id);

create index idx_equipment_id_schedule
    on task_schedule (equipment_id);

create index idx_start_time_schedule
    on task_schedule (start_time);

create table equipment_capability
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    equipment_id        bigint                             not null comment '设备ID',
    equipment_name      varchar(100)                       not null comment '设备名称',
    shadow_mode         varchar(32)                        null comment '影随模式',
    max_concurrent_tasks int                               null comment '最大同时任务数',
    current_task_count  int                               null comment '当前任务数',
    supported_task_types json                              null comment '支持的任务类型',
    load_threshold      double                             null comment '资源负载阈值(%)',
    current_load        double                             null comment '当前负载(%)',
    status              varchar(32)                        null comment '状态：ACTIVE-活跃, INACTIVE-不活跃, MAINTENANCE-维护中',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_equipment_capability_equipment
        foreign key (equipment_id) references t_ground_equipment (id)
)
    comment '设备能力';

create index idx_equipment_id_capability
    on equipment_capability (equipment_id);

create index idx_status_capability
    on equipment_capability (status);

create table shadow_relation
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    primary_schedule_id bigint                             not null comment '主任务调度ID',
    shadow_schedule_id  bigint                             not null comment '影随任务调度ID',
    shadow_mode         varchar(32)                        null comment '影随模式',
    priority            int                                null comment '优先级',
    resource_occupancy  double                             null comment '资源占用率(%)',
    splittable          tinyint(1)                         null comment '是否可拆分：0-否，1-是',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_shadow_relation_primary
        foreign key (primary_schedule_id) references task_schedule (id),
    constraint fk_shadow_relation_shadow
        foreign key (shadow_schedule_id) references task_schedule (id)
)
    comment '影随关系';

create index idx_primary_schedule_id
    on shadow_relation (primary_schedule_id);

create index idx_shadow_schedule_id
    on shadow_relation (shadow_schedule_id);

