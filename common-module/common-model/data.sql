-- t_constraint 表数据
INSERT INTO t_constraint (type_id, name, constraint_type, description, min_value, max_value, data_type, required, sort_no, create_time, update_time, is_enabled)
VALUES
(1, '时间约束', 'TIME', '任务执行时间约束', 0, 86400, 1, 1, 1, NOW(), NOW(), 1),
(1, '频率约束', 'RESOURCE', '设备频率约束', 2000, 18000, 1, 1, 2, NOW(), NOW(), 1),
(2, '可见时长约束', 'VISIBILITY', '卫星可见时长约束', 300, 1800, 1, 1, 3, NOW(), NOW(), 1),
(2, '天线仰角约束', 'EQUIPMENT', '天线仰角约束', 5, 90, 1, 1, 4, NOW(), NOW(), 1),
(3, '优先级约束', 'PRIORITY', '任务优先级约束', 1, 3, 1, 1, 5, NOW(), NOW(), 1);

-- t_ground_station 表数据
INSERT INTO t_ground_station (station_name, station_code, station_type, location, latitude, longitude, altitude, status, description)
VALUES
('北京密云站', 'BJ-MY-01', '跟踪测控站', '北京市密云区', 40.3757, 116.8363, 175.6, 'ACTIVE', '北京密云地面站，S/X频段接收能力'),
('喀什站', 'KS-01', '数据接收站', '新疆喀什地区', 39.5045, 76.0300, 1320.5, 'ACTIVE', '喀什数据接收站，多频段接收能力'),
('三亚站', 'SY-01', '综合站', '海南省三亚市', 18.2525, 109.5108, 15.8, 'ACTIVE', '三亚综合站，测控与数据接收'),
('南极中山站', 'ZS-01', '极地站', '南极中山站', -69.3778, 76.3683, 45.2, 'MAINTENANCE', '南极中山站，极地观测能力'),
('昆明站', 'KM-01', '跟踪测控站', '云南省昆明市', 25.0330, 102.7120, 1895.0, 'ACTIVE', '昆明站，主要负责GEO卫星测控');

-- t_ground_equipment 表数据
INSERT INTO t_ground_equipment (ground_station_id, equipment_name, equipment_code, equipment_type, min_frequency, max_frequency, frequency_band, polarization, gain, status, description)
VALUES
(1, 'S频段天线', 'BJ-MY-S-01', '测控天线', 2200, 2300, 'S频段', '圆极化', 35.6, 'ACTIVE', 'S频段跟踪测控天线'),
(1, 'X频段天线', 'BJ-MY-X-01', '数据接收天线', 8000, 8400, 'X频段', '双线性极化', 42.3, 'ACTIVE', 'X频段高速数据接收天线'),
(2, 'Ka频段天线', 'KS-KA-01', '高速数据接收', 26000, 40000, 'Ka频段', '圆极化', 52.1, 'ACTIVE', 'Ka频段高速数据接收系统'),
(2, 'S频段测控设备', 'KS-S-01', '测控设备', 2025, 2110, 'S频段', '右旋圆极化', 39.8, 'ACTIVE', 'S频段上行测控设备'),
(3, 'C频段天线', 'SY-C-01', '数据接收天线', 3600, 4200, 'C频段', '双线性极化', 38.5, 'ACTIVE', 'C频段中速数据接收天线'),
(3, 'X/Ka双频段天线', 'SY-XKA-01', '高速数据接收', 8000, 30000, 'X/Ka双频段', '圆极化', 56.2, 'MAINTENANCE', 'X/Ka双频段高速数据接收系统'),
(4, 'VHF设备', 'ZS-VHF-01', '极地通信设备', 137, 144, 'VHF频段', '线性极化', 12.5, 'ACTIVE', '极地VHF通信设备'),
(5, 'L频段接收系统', 'KM-L-01', '跟踪系统', 1525, 1559, 'L频段', '右旋圆极化', 30.2, 'ACTIVE', 'L频段跟踪接收系统');

-- t_satellite 表数据
INSERT INTO t_satellite (satellite_name_cn, satellite_name_en, satellite_code, cospar_id, norad_id, orbit_type, semi_major_axis, eccentricity, inclination, right_ascension, argument_of_perigee, mean_anomaly, epoch_time, tle_line1, tle_line2, launch_time, status, description, tags)
VALUES
('高分一号', 'GF-1', 'GF-01', '2013-018A', '39150', 'LEO', 7147.0, 0.00146, 98.06, 129.5, 99.78, 261.36, '2023-01-01 00:00:00', '1 39150U 13018A   23001.00000000  .00000073  00000-0  10000-4 0  9993', '2 39150  98.0600 129.5000 0014600  99.7800 261.3600 14.76061573524835', '2013-04-26 04:13:00', 'OPERATIONAL', '高分一号是中国首颗亚米级高分辨率对地观测卫星', '["观测", "遥感", "LEO"]'),
('北斗三号G1', 'BDS-3 G1', 'BDS3-G1', '2018-003A', '43207', 'GEO', 42164.0, 0.00008, 0.3, 110.5, 180.0, 0.0, '2023-01-01 00:00:00', '1 43207U 18003A   23001.00000000 -.00000348  00000-0  00000+0 0  9995', '2 43207   0.3000 110.5000 0000800 180.0000   0.0000  1.00273780 18230', '2018-01-12 23:18:00', 'OPERATIONAL', '北斗三号G1卫星，地球静止轨道卫星', '["导航", "GEO", "北斗"]'),
('风云四号B星', 'FY-4B', 'FY-4B', '2021-053A', '48895', 'GEO', 42164.0, 0.00003, 0.1, 123.5, 90.0, 270.0, '2023-01-01 00:00:00', '1 48895U 21053A   23001.00000000 -.00000305  00000-0  00000+0 0  9991', '2 48895   0.1000 123.5000 0000300  90.0000 270.0000  1.00271553  6340', '2021-06-02 17:17:00', 'OPERATIONAL', '风云四号B星，地球静止轨道气象卫星', '["气象", "GEO", "观测"]'),
('海洋二号D', 'HY-2D', 'HY-2D', '2021-043A', '48881', 'LEO', 7356.0, 0.00085, 66.0, 210.5, 95.4, 265.1, '2023-01-01 00:00:00', '1 48881U 21043A   23001.00000000  .00000018  00000-0  22318-4 0  9990', '2 48881  66.0000 210.5000 0008500  95.4000 265.1000 14.33623170 10125', '2021-05-19 01:03:00', 'OPERATIONAL', '海洋二号D星，海洋动力环境监测卫星', '["海洋", "遥感", "LEO"]'),
('天链一号05星', 'TL-1-05', 'TL-1-05', '2020-095A', '46027', 'GEO', 42164.0, 0.00006, 0.05, 80.2, 180.0, 0.0, '2023-01-01 00:00:00', '1 46027U 20095A   23001.00000000 -.00000290  00000-0  00000+0 0  9997', '2 46027   0.0500  80.2000 0000600 180.0000   0.0000  1.00270000  9520', '2020-12-30 16:45:00', 'OPERATIONAL', '天链一号05星，空间数据中继卫星', '["中继", "GEO", "通信"]');

-- t_satellite_group 表数据
INSERT INTO t_satellite_group (group_name, group_desc, group_status, create_time, update_time)
VALUES
('高分卫星组', '高分系列对地观测卫星', 'ACTIVE', NOW(), NOW()),
('北斗导航系统', '北斗全球卫星导航系统', 'ACTIVE', NOW(), NOW()),
('风云气象卫星', '风云系列气象观测卫星', 'ACTIVE', NOW(), NOW()),
('海洋监测卫星组', '海洋环境监测卫星系列', 'ACTIVE', NOW(), NOW()),
('通信中继卫星组', '天链系列数据中继卫星', 'ACTIVE', NOW(), NOW());

-- t_payload 表数据
INSERT INTO t_payload (satellite_id, payload_name, payload_type, payload_code, status, description)
VALUES
(1, '2米全色/8米多光谱相机', '光学相机', 'GF1-PMS', 'OPERATIONAL', '2米全色/8米多光谱相机，可见光与近红外波段观测'),
(1, '16米多光谱相机', '光学相机', 'GF1-WFI', 'OPERATIONAL', '16米分辨率多光谱宽幅相机'),
(2, '导航载荷', 'GNSS信号发生器', 'BDS3-NAV', 'OPERATIONAL', '北斗三号卫星导航信号发生与传输设备'),
(2, '星间链路天线', '通信设备', 'BDS3-ISL', 'OPERATIONAL', '北斗卫星间链路通信设备'),
(3, '多通道扫描成像辐射计', '气象传感器', 'FY4B-MCSI', 'OPERATIONAL', '多通道扫描成像辐射计，用于气象观测'),
(3, '闪电成像仪', '光学传感器', 'FY4B-LMI', 'OPERATIONAL', '闪电成像仪，用于监测闪电活动'),
(4, '雷达高度计', '雷达设备', 'HY2D-ALT', 'OPERATIONAL', '雷达高度计，用于测量海面高度'),
(4, '微波散射计', '微波设备', 'HY2D-SCAT', 'OPERATIONAL', '微波散射计，用于测量海面风场'),
(5, 'Ka频段通信系统', '通信设备', 'TL1-KACS', 'OPERATIONAL', 'Ka频段高速数据中继通信系统'),
(5, 'S频段测控系统', '测控设备', 'TL1-STCS', 'OPERATIONAL', 'S频段卫星测控通信系统');

-- task_comment 表数据
INSERT INTO task_comment (task_id, comment_content, creator_id, create_time)
VALUES
('TASK202301001', '任务执行正常，数据接收完成', 'USER001', '2023-01-10 09:15:32'),
('TASK202301001', '数据质量评估完成，符合预期', 'USER002', '2023-01-10 14:28:45'),
('TASK202301002', '任务执行延迟，需要进行时间调整', 'USER003', '2023-01-12 08:43:21'),
('TASK202301003', '任务与其他任务存在资源冲突，需要协调', 'USER002', '2023-01-15 10:37:56'),
('TASK202301004', '任务紧急性提升，请优先保障执行', 'USER001', '2023-01-18 16:05:12');

-- task_constraint 表数据
INSERT INTO task_constraint (task_id, constraint_type, name, description, min_value, max_value, data_type, is_enabled, required, sort_no, create_time, update_time)
VALUES
(1, 'TIME', '执行时长约束', '任务执行时间不超过30分钟', 600, 1800, 1, 1, 1, 1, NOW(), NOW()),
(1, 'VISIBILITY', '可见性约束', '卫星可见时间不少于10分钟', 600, NULL, 1, 1, 1, 2, NOW(), NOW()),
(2, 'RESOURCE', '带宽约束', '数据传输带宽不低于50Mbps', 50, NULL, 1, 1, 1, 1, NOW(), NOW()),
(3, 'EQUIPMENT', '天线约束', '需要使用X频段天线', NULL, NULL, 2, 1, 1, 1, NOW(), NOW()),
(4, 'ORBITAL', '轨道约束', '卫星高度在500-800km之间', 500, 800, 1, 1, 1, 1, NOW(), NOW());

-- task_info 表数据
INSERT INTO task_info (task_id, task_name, task_desc, priority_level, importance_level, task_mode_id, task_type, task_status, parent_task_id, start_time, end_time, task_satellite_ids, target_satellite_ids, create_time, update_time, ground_station_ids)
VALUES
('TASK202301001', '高分一号成像任务', '对青海湖区域进行高分辨率成像', 3, 3, 'MODE001', 'IMAGING', 'COMPLETED', NULL, '2023-01-10 08:30:00', '2023-01-10 08:45:00', '[1]', NULL, '2023-01-05 10:00:00', '2023-01-10 09:00:00', '[1,2]'),
('TASK202301002', '北斗导航测试任务', '北斗三号G1卫星导航信号测试', 2, 2, 'MODE002', 'NAVIGATION', 'COMPLETED', NULL, '2023-01-12 09:00:00', '2023-01-12 11:00:00', '[2]', NULL, '2023-01-08 14:30:00', '2023-01-12 11:30:00', '[3,5]'),
('TASK202301003', '风云四号数据接收', '接收风云四号B星气象数据', 3, 2, 'MODE003', 'DATA_RECEIVE', 'SCHEDULED', NULL, '2023-01-20 14:00:00', '2023-01-20 14:30:00', '[3]', NULL, '2023-01-15 09:45:00', '2023-01-15 09:45:00', '[2]'),
('TASK202301004', '海洋二号数据接收', '接收海洋二号D星海洋环境监测数据', 2, 2, 'MODE001', 'DATA_RECEIVE', 'SCHEDULED', NULL, '2023-01-22 10:15:00', '2023-01-22 10:45:00', '[4]', NULL, '2023-01-18 16:00:00', '2023-01-18 16:00:00', '[3]'),
('TASK202301005', '卫星间链路测试', '天链一号05星与高分一号链路测试', 3, 3, 'MODE002', 'RELAY', 'PENDING', NULL, '2023-01-25 09:30:00', '2023-01-25 10:00:00', '[5]', '[1]', '2023-01-20 11:20:00', '2023-01-20 11:20:00', '[1]');

-- task_mode 表数据
INSERT INTO task_mode (task_template_id, mode_name, mode_type, execution_time, period, task_condition, create_time, update_time)
VALUES
(1, '单次定时执行', 'SCHEDULED', '08:30', NULL, NULL, NOW(), NOW()),
(1, '每日周期执行', 'PERIODIC', '10:00', 1440, NULL, NOW(), NOW()),
(2, '条件触发执行', 'CONDITIONAL', NULL, NULL, '可见时间>15分钟 AND 天气状况=晴朗', NOW(), NOW()),
(2, '六小时周期执行', 'PERIODIC', '00:00', 360, NULL, NOW(), NOW()),
(3, '紧急任务执行', 'CONDITIONAL', NULL, NULL, '紧急级别=高 AND 资源可用=是', NOW(), NOW());

-- task_template 表数据
INSERT INTO task_template (template_id, template_name, template_desc, is_enabled, task_modes, task_satellite_ids, target_satellite_ids, create_time, update_time, task_constraint_ids, ground_station_ids)
VALUES
('TPL001', '标准成像任务', '高分卫星标准成像任务模板', 1, '[1,2]', '[1]', NULL, NOW(), NOW(), '[1,2]', '[1,2]'),
('TPL002', '导航信号测试', '北斗卫星导航信号测试任务模板', 1, '[3]', '[2]', NULL, NOW(), NOW(), '[3]', '[3,5]'),
('TPL003', '气象数据接收', '风云卫星气象数据常规接收任务', 1, '[2,4]', '[3]', NULL, NOW(), NOW(), '[4]', '[2]'),
('TPL004', '海洋环境监测', '海洋卫星数据接收与处理任务', 1, '[1,4]', '[4]', NULL, NOW(), NOW(), '[1,3]', '[3]'),
('TPL005', '卫星中继通信', '天链卫星中继通信与数据传输任务', 1, '[3,5]', '[5]', '[1,4]', NOW(), NOW(), '[2,3]', '[1]');

-- task_template_constraint 表数据
INSERT INTO task_template_constraint (task_template_id, constraint_type, name, description, min_value, max_value, data_type, is_enabled, required, sort_no, create_time, update_time)
VALUES
(1, 'TIME', '执行时长约束', '任务执行时间约束', 600, 1800, 1, 1, 1, 1, NOW(), NOW()),
(1, 'VISIBILITY', '可见性约束', '卫星可见时间约束', 600, NULL, 1, 1, 1, 2, NOW(), NOW()),
(2, 'RESOURCE', '带宽约束', '数据传输带宽约束', 50, NULL, 1, 1, 1, 1, NOW(), NOW()),
(2, 'EQUIPMENT', '天线约束', '任务所需天线类型约束', NULL, NULL, 2, 1, 1, 2, NOW(), NOW()),
(3, 'ORBITAL', '轨道约束', '卫星轨道参数约束', 500, 800, 1, 1, 1, 1, NOW(), NOW());

-- task_schedule 表数据
INSERT INTO task_schedule (task_id, ground_station_id, equipment_id, start_time, end_time, status, has_shadow_relation, is_preempted, description, create_time, update_time)
VALUES
(1, 1, 1, '2023-01-10 08:30:00', '2023-01-10 08:45:00', 'COMPLETED', 0, 0, '高分一号成像任务调度计划', NOW(), NOW()),
(2, 3, 5, '2023-01-12 09:00:00', '2023-01-12 11:00:00', 'COMPLETED', 0, 0, '北斗三号导航测试任务调度计划', NOW(), NOW()),
(3, 2, 3, '2023-01-20 14:00:00', '2023-01-20 14:30:00', 'SCHEDULED', 1, 0, '风云四号数据接收任务调度计划', NOW(), NOW()),
(4, 3, 5, '2023-01-22 10:15:00', '2023-01-22 10:45:00', 'SCHEDULED', 0, 0, '海洋二号数据接收任务调度计划', NOW(), NOW()),
(5, 1, 2, '2023-01-25 09:30:00', '2023-01-25 10:00:00', 'PENDING', 1, 0, '卫星间链路测试任务调度计划', NOW(), NOW());

-- equipment_capability 表数据
INSERT INTO equipment_capability (equipment_id, equipment_name, shadow_mode, max_concurrent_tasks, current_task_count, supported_task_types, load_threshold, current_load, status, create_time, update_time)
VALUES
(1, 'S频段天线', 'PASSIVE', 2, 0, '["TRACKING", "CONTROL"]', 80.0, 25.5, 'ACTIVE', NOW(), NOW()),
(2, 'X频段天线', 'ACTIVE', 3, 1, '["DATA_RECEIVE", "IMAGING"]', 85.0, 45.2, 'ACTIVE', NOW(), NOW()),
(3, 'Ka频段天线', 'ACTIVE', 2, 1, '["DATA_RECEIVE", "RELAY"]', 90.0, 60.3, 'ACTIVE', NOW(), NOW()),
(4, 'S频段测控设备', 'PASSIVE', 1, 0, '["CONTROL", "TRACKING"]', 75.0, 15.8, 'ACTIVE', NOW(), NOW()),
(5, 'C频段天线', 'NONE', 2, 1, '["DATA_RECEIVE"]', 80.0, 35.6, 'ACTIVE', NOW(), NOW());

-- shadow_relation 表数据
INSERT INTO shadow_relation (primary_schedule_id, shadow_schedule_id, shadow_mode, priority, resource_occupancy, splittable, create_time, update_time)
VALUES
(3, 5, 'ACTIVE', 1, 65.0, 1, NOW(), NOW()),
(5, 3, 'PASSIVE', 2, 35.0, 0, NOW(), NOW());
