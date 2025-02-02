package com.example.protocol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * 协议数据区域。
 * 分两种情况：
 * 1. 没有叶子节点，即消息体中又包含一层协议。header和body属性就代表了该层协议的头部和主体,check属性代表了该层协议的校验信息
 * 2. 有叶子节点，即消息体中有多个叶子节点。
 */
@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Body extends BaseNode{

    /**
     * 节点列表。
      */    
    @XmlElement(name = "node")
    private List<Node> nodes;

    /**
     * 如果消息体中又包含一层协议，header和body属性就代表了该层协议的头部和主体。
     */
    @XmlElement(name = "head")
    private Header header;

    @XmlElement(name = "body")
    private Body body;


    @XmlElement(name = "check")
    private Node check;

} 