package com.satellite.protocol.model;

import javax.xml.bind.annotation.*;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class EnumValue {
    @XmlAttribute
    private String value;
    
    @XmlValue
    private String description;
    
    // getters and setters

  public void setValue(String value) {
        this.value = value;
    }

  public void setDescription(String description) {
        this.description = description;
    }
} 