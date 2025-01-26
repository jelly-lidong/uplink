package com.example.protocol.model;

import javax.xml.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 节点类，表示协议中的一个节点。
 */
@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node extends BaseNode {

    @XmlElement(name = "depOnNodeName")
    private String depOnNodeName;

    @XmlElement(name = "depOnBody")
    private Boolean depOnBody;

//    @XmlElement(name = "depOnNodeCalFuncName")
//    private String depOnNodeCalFuncName;
//
//    @XmlElement(name = "depOnNodeCalFuncExp")
//    private String depOnNodeCalFuncExp;

    @XmlElement(name = "calFuncName")
    private String calFuncName;

    @XmlElement(name = "calFuncExp")
    private String calFuncExp;

    @XmlElement(name = "reverseCalFuncName")
    private String reverseCalFuncName;

    @XmlElement(name = "reverseCalFuncExp")
    private String reverseCalFuncExp;

    @XmlTransient
    private Body onBody;

}