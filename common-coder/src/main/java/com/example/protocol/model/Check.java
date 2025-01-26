package com.example.protocol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * 检查类，表示协议的检查结构。
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Check {
    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "node")
    private List<Node> nodes;

    // Getter 和 Setter
} 