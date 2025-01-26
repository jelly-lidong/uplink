package com.example.protocol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * 主体类，表示协议的主体结构。
 */
@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Body extends BaseNode{
    @XmlElement(name = "node")
    private List<Node> nodes;

    @XmlElement(name = "head")
    private Header header;

    @XmlElement(name = "body")
    private Body body;

    @XmlElement(name = "check")
    private Check check;

} 