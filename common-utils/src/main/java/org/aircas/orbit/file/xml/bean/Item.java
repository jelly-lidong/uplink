package org.aircas.orbit.file.xml.bean;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Item")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "type",
    "name",
    "webValue",
    "textValue"

})
public class Item {

  @XmlElement(name = "Type", required = true)
  private String type;

  @XmlElement(name = "Name", required = true)
  private String name;

  @XmlElement(name = "Text", required = true)
  private TextValue textValue;

  @XmlElement(name = "Web", required = true)
  private WebValue webValue;
}


