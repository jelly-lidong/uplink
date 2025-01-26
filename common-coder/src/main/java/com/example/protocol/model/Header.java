package com.example.protocol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * 头部类，表示协议的头部结构。
 */
@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header extends BaseNode {

    @XmlElement(name = "node")
    private List<Node> nodes;

    // Getter 和 Setter
} 