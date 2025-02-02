package com.example.protocol.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * 协议类，表示整个协议结构。
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "protocol")
public class Protocol {

  @XmlElement(name = "header")
  private Header header; // 单个 Header

  @XmlElement(name = "body")
  private Body body; // 单个 Body

  @XmlElement(name = "check")
  private Node check; // 单个 Check

}