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

    /**
     * 该节点是否可选。
     */
    @XmlElement(name = "isOptional")
    private Boolean isOptional;

    /**
     * 该节点依赖的节点名称。
     */
    @XmlElement(name = "depOnNodeName")
    private String depOnNodeName;

    /**
     * 该节点依赖的节点。如果该节点没有依赖，则该字段是null，否则该字段是依赖的节点对象，可以是Header、Body、BaseNode。
     */
    @XmlTransient
    private BaseNode depOnNode;

    /**
     * 该节点的计算函数名称。
     */
    @XmlElement(name = "calFuncName")
    private String calFuncName;

    /**
     * 该节点的计算函数表达式。
     */
    @XmlElement(name = "calFuncExp")
    private String calFuncExp;

    /**
     * 该节点的反向计算函数名称。
     */
    @XmlElement(name = "reverseCalFuncName")
    private String reverseCalFuncName;

    /**
     * 该节点的反向计算函数表达式。
     */
    @XmlElement(name = "reverseCalFuncExp")
    private String reverseCalFuncExp;



}