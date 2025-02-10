package org.aircas.orbit.util.file.xml.bean;


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
@XmlRootElement(name = "Web")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "title",
    "url"
})
public class WebValue {

  @XmlElement(name = "Title", required = true)
  private String title;
  @XmlElement(name = "Url", required = true)
  private String url;

}
