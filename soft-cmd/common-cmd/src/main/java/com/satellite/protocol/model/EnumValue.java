package com.satellite.protocol.model;

import javax.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class EnumValue {
    @XmlAttribute
    private String value;  // 实际值，如"0x01"或"0b01"
    
    @XmlValue
    private String name;   // 枚举名称，如"温度采集"
} 