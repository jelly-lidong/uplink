package com.satellite.protocol.model;

import com.satellite.protocol.model.adapter.LengthUnitAdapter;
import com.satellite.protocol.model.enums.LengthUnit;
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
public class ProtocolBody {

  @XmlAttribute
  private String name;
  @XmlAttribute
  private Integer length;

  @XmlAttribute
  @XmlJavaTypeAdapter(LengthUnitAdapter.class)
  private LengthUnit lengthUnit;

  @XmlElement(name = "header")
  private ProtocolHeader header;

  @XmlElement(name = "body")
  private ProtocolBody subBody;

  @XmlElement(name = "check")
  private ProtocolCheck check;

} 