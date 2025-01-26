-- 卫星信息表
CREATE TABLE IF NOT EXISTS t_satellite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '卫星ID',
    satellite_name_cn VARCHAR(100) NOT NULL COMMENT '卫星中文名称',
    satellite_name_en VARCHAR(100) NOT NULL COMMENT '卫星英文名称',
    satellite_code VARCHAR(50) UNIQUE COMMENT '卫星编号',
    cospar_id VARCHAR(20) COMMENT '国际编号(COSPAR ID)',
    norad_id VARCHAR(20) COMMENT 'NORAD编号',
    orbit_type VARCHAR(50) COMMENT '轨道类型',
    semi_major_axis DOUBLE COMMENT '半长轴(千米)',
    eccentricity DOUBLE COMMENT '偏心率',
    inclination DOUBLE COMMENT '轨道倾角(度)',
    right_ascension DOUBLE COMMENT '升交点赤经(度)',
    argument_of_perigee DOUBLE COMMENT '近地点幅角(度)',
    mean_anomaly DOUBLE COMMENT '平近点角(度)',
    epoch_time DATETIME COMMENT '历元时间',
    tle_line1 VARCHAR(100) COMMENT '两行根数集第一行',
    tle_line2 VARCHAR(100) COMMENT '两行根数集第二行',
    launch_time DATETIME COMMENT '发射时间',
    status VARCHAR(20) COMMENT '卫星状态',
    description TEXT COMMENT '描述信息',
    tags JSON COMMENT '标签列表',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_satellite_code (satellite_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卫星信息';

-- 卫星载荷表
CREATE TABLE IF NOT EXISTS t_payload (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '载荷ID',
    satellite_id BIGINT NOT NULL COMMENT '所属卫星ID',
    payload_name VARCHAR(100) NOT NULL COMMENT '载荷名称',
    payload_type VARCHAR(50) COMMENT '载荷类型',
    payload_code VARCHAR(50) UNIQUE COMMENT '载荷编号',
    status VARCHAR(20) COMMENT '载荷状态',
    description TEXT COMMENT '描述信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_satellite_id (satellite_id),
    INDEX idx_payload_code (payload_code),
    FOREIGN KEY (satellite_id) REFERENCES t_satellite(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卫星载荷信息';

-- 地面站表
CREATE TABLE IF NOT EXISTS t_ground_station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地面站ID',
    station_name VARCHAR(100) NOT NULL COMMENT '地面站名称',
    station_code VARCHAR(50) UNIQUE COMMENT '地面站编号',
    station_type VARCHAR(50) COMMENT '地面站类型',
    location VARCHAR(200) COMMENT '地理位置',
    latitude DOUBLE COMMENT '纬度',
    longitude DOUBLE COMMENT '经度',
    altitude DOUBLE COMMENT '海拔高度(米)',
    status VARCHAR(20) COMMENT '站点状态',
    description TEXT COMMENT '描述信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_station_code (station_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地面站信息';

-- 地面站装备表
CREATE TABLE IF NOT EXISTS t_ground_equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '装备ID',
    ground_station_id BIGINT NOT NULL COMMENT '所属地面站ID',
    equipment_name VARCHAR(100) NOT NULL COMMENT '装备名称',
    equipment_code VARCHAR(50) UNIQUE COMMENT '装备编号',
    equipment_type VARCHAR(50) COMMENT '装备类型',
    min_frequency DOUBLE COMMENT '最小工作频率(MHz)',
    max_frequency DOUBLE COMMENT '最大工作频率(MHz)',
    frequency_band VARCHAR(20) COMMENT '频段',
    polarization VARCHAR(50) COMMENT '极化方式',
    gain DOUBLE COMMENT '天线增益(dB)',
    status VARCHAR(20) COMMENT '装备状态',
    description TEXT COMMENT '描述信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_ground_station_id (ground_station_id),
    INDEX idx_equipment_code (equipment_code),
    INDEX idx_status (status),
    FOREIGN KEY (ground_station_id) REFERENCES t_ground_station(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地面站装备信息'; 