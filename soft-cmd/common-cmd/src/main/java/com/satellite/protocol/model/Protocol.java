package com.satellite.protocol.model;

import com.satellite.protocol.model.adapter.LengthUnitAdapter;
import com.satellite.protocol.model.enums.LengthUnit;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlRootElement(name = "protocol")
@XmlAccessorType(XmlAccessType.FIELD)
public class Protocol {
    @XmlAttribute
    private String name;

    @XmlAttribute
    private Integer length;

    /**
     * 长度单位.
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(LengthUnitAdapter.class)
    private LengthUnit lengthUnit = LengthUnit.BYTE;
    
    @XmlElement(name = "header")
    private ProtocolHeader header;
    
    @XmlElement(name = "body")
    private ProtocolBody body;
    
    @XmlElement(name = "check")
    private ProtocolCheck check;

    @XmlAttribute
    private String version;  // 协议版本号

    @XmlAttribute
    private String description;  // 协议描述

} 