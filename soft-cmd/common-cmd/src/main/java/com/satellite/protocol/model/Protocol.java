package com.satellite.protocol.model;

import javax.xml.bind.annotation.*;
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
    
    @XmlElement(name = "header")
    private ProtocolHeader header;
    
    @XmlElement(name = "body")
    private ProtocolBody body;
    
    @XmlElement(name = "check")
    private ProtocolCheck check;

} 