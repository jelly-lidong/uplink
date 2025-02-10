package org.aircas.orbit.util.file.xml.bean;


import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aircas.orbit.util.file.xml.adapter.JaxbDateAdapter;
import org.aircas.orbit.util.file.xml.adapter.JaxbNumberAdapter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "toUserName",
    "fromUserName",
    "createTime",
    "msgType",
    "event",
    "changeType",
    "userID",
    "name",
    "department",
    "mainDepartment",
    "isLeaderInDept",
    "directLeader",
    "position",
    "mobile",
    "gender",
    "email",
    "bizMail",
    "status",
    "avatar",
    "alias",
    "telephone",
    "address",
    "items",


})
public class MemberXml {

  @XmlElement(name = "ToUserName", required = true)
  private String toUserName;
  @XmlElement(name = "FromUserName", required = true)
  private String fromUserName;
  @XmlElement(name = "CreateTime", required = true)
  @XmlJavaTypeAdapter(JaxbDateAdapter.class) // 如果不生效就放在get方法上
  private Date createTime;
  @XmlElement(name = "MsgType", required = true)
  private String msgType;
  @XmlElement(name = "Event", required = true)
  private String event;
  @XmlElement(name = "ChangeType", required = true)
  private String changeType;
  @XmlElement(name = "UserID", required = true)
  private String userID;
  @XmlElement(name = "Name", required = true)
  private String name;
  @XmlElement(name = "Department", required = true)
  private String department;
  @XmlElement(name = "MainDepartment", required = true)
  private String mainDepartment;
  @XmlElement(name = "IsLeaderInDept", required = true)
  private String isLeaderInDept;
  @XmlElement(name = "DirectLeader", required = true)
  private String directLeader;
  @XmlElement(name = "Position", required = true)
  private String position;
  @XmlElement(name = "Mobile", required = true)
  private String mobile;
  @XmlElement(name = "Gender", required = true)
  private String gender;
  @XmlElement(name = "Email", required = true)
  private String email;
  @XmlElement(name = "BizMail", required = true)
  private String bizMail;
  @XmlElement(name = "Status", required = true)
  private String status;
  @XmlElement(name = "Avatar", required = true)
  private String avatar;
  @XmlElement(name = "Alias", required = true)
  private String alias;
  @XmlElement(name = "Telephone", required = true)
  private String telephone;
  @XmlElement(name = "Address", required = true)
  private String address;
  @XmlElement(name = "Num", required = true)
  @XmlJavaTypeAdapter(JaxbNumberAdapter.class)
  private Double num;
  @XmlElementWrapper(name = "ExtAttr", required = true)
  @XmlElement(name = "Item", required = true)
  private List<Item> items;

}


