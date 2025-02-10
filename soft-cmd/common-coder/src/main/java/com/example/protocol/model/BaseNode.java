package com.example.protocol.model;

import com.example.protocol.enums.EndianType;
import com.example.protocol.enums.LengthUnit;
import com.example.protocol.enums.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseNode {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "length")
    private Integer length;

    @XmlAttribute(name = "lengthUnit")
    private LengthUnit lengthUnit;

    @XmlAttribute(name = "valueType")
    private ValueType valueType;

    @XmlAttribute(name = "value")
    private String value;

    @XmlAttribute(name = "endianType")
    private EndianType endianType = EndianType.BIG_BYTE;
}
