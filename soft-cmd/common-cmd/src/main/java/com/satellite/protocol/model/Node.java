package com.satellite.protocol.model;

import com.satellite.protocol.model.adapter.LengthUnitAdapter;
import com.satellite.protocol.model.adapter.NodeTypeAdapter;
import com.satellite.protocol.model.enums.LengthUnit;
import com.satellite.protocol.model.enums.NodeType;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Node {

  @XmlAttribute
  private String name;

  @XmlAttribute
  private int length;

  @XmlAttribute
  @XmlJavaTypeAdapter(LengthUnitAdapter.class)
  private LengthUnit lengthUnit;

  @XmlAttribute
  @XmlJavaTypeAdapter(NodeTypeAdapter.class)
  private NodeType type;

  @XmlAttribute
  private String defValue;

  @XmlAttribute
  private String refValue;

  @XmlAttribute
  private String convert;

  @XmlAttribute
  private Boolean optional;
  @XmlAttribute
  private String checkRange;

  @XmlElement(name = "enum")
  private List<EnumValue> enumValues;

} 