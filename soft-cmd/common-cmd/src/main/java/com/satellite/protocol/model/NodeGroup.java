package com.satellite.protocol.model;

import com.satellite.protocol.model.adapter.LengthUnitAdapter;
import com.satellite.protocol.model.enums.LengthUnit;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlTransient;

@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeGroup {
  /**
   * 节点组名称.
   */
  @XmlAttribute
  private String name;

  /**
   * 节点组重复次数，支持表达式，如：${dataCount}
   */
  @XmlAttribute
  private String repeat;

  @XmlAttribute
  private Integer length;

  /**
   * 节点长度单位.
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(LengthUnitAdapter.class)
  private LengthUnit lengthUnit = LengthUnit.BYTE;

  @XmlElement(name = "node")
  private List<Node> nodes;

  @XmlTransient
  private ProtocolBody body;

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
    if (nodes != null) {
      nodes.forEach(node -> node.setGroup(this));
    }
  }
}
