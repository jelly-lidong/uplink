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
public class ProtocolBody {

  @XmlAttribute
  private String name;
  @XmlAttribute
  private Integer length;

  @XmlAttribute
  @XmlJavaTypeAdapter(LengthUnitAdapter.class)
  private LengthUnit lengthUnit;

  @XmlTransient
  private Protocol protocol;

  @XmlElement(name = "header")
  private ProtocolHeader header;

  @XmlElement(name = "body")
  private ProtocolBody subBody;

  @XmlElement(name = "check")
  private ProtocolCheck check;

  @XmlElement(name = "node")
  private List<Node> nodes;

  @XmlElement(name = "nodeGroup")
  private List<NodeGroup> nodeGroups;

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
    if (nodes != null) {
      nodes.forEach(node -> node.setBody(this));
    }
  }

  public void setNodeGroups(List<NodeGroup> nodeGroups) {
    this.nodeGroups = nodeGroups;
    if (nodeGroups != null) {
      nodeGroups.forEach(group -> group.setBody(this));
    }
  }

} 