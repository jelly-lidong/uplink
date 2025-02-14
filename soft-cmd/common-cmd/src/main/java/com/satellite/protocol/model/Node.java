package com.satellite.protocol.model;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.expression.Expression;
import com.satellite.protocol.core.expression.ExpressionContext;
import com.satellite.protocol.core.expression.ExpressionFactory;
import com.satellite.protocol.model.adapter.LengthUnitAdapter;
import com.satellite.protocol.model.adapter.NodeTypeAdapter;
import com.satellite.protocol.model.enums.LengthUnit;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.model.enums.ByteOrder;
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
public class Node {

  /**
   * 节点名称.
   */
  @XmlAttribute
  private String name;

  /**
   * 节点长度，支持表达式，如：${body}.length - ${header}.length
   */
  @XmlAttribute
  private String length;

  /**
   * 节点长度单位.
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(LengthUnitAdapter.class)
  private LengthUnit lengthUnit;

  /**
   * 节点类型.
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(NodeTypeAdapter.class)
  private NodeType type;

  /**
   * 节点默认值.
   */
  @XmlAttribute
  private String defValue;

  /**
   * 节点引用值，用于引用其他节点的值.支持某些表达式，如：${node1}.value，${node2}.length,${node2}.length -1 etc.
   * 表达式${node1},表示引用节点node1,node1为相对当前节点的xml节点路径，like:../node1
   * 表达式支持扩展
   */
  @XmlAttribute
  private String refValue;

  /**
   * 节点转换器，用于将节点值转换为其他值，如：day(x - '2020-01-01 00:00:00') -1,x为节点值，day为转换器名称，-1为转换参数，再如：millSeconds(x - 'currentDate') -1，x为节点值，millSeconds为转换器名称，-1为转换参数；
   * 转换器支持扩展
   */
  @XmlAttribute
  private String convert;

  /**
   * 节点是否可选，默认false.
   */
  @XmlAttribute
  private Boolean optional;


  @XmlElement(name = "enum")
  private List<EnumValue> enumValues;

  /**
   * 字节序.
   */
  @XmlAttribute
  private ByteOrder byteOrder = ByteOrder.BIG;

  /**
   * 节点描述.
   */
  @XmlAttribute
  private String description;

  /**
   * 值校验表达式.
   */
  @XmlAttribute
  private String validation;

  private Object value;  // 添加value字段存储实际值

  /**
   * 所属协议头
   */
  @XmlTransient
  private ProtocolHeader header;
  
  /**
   * 所属协议体
   */
  @XmlTransient
  private ProtocolBody body;
  
  /**
   * 所属节点组
   */
  @XmlTransient
  private NodeGroup group;
  
  /**
   * 获取节点所在的协议对象
   */
  public Protocol getProtocol() {
    if (header != null) {
      return header.getProtocol();
    }
    if (body != null) {
      return body.getProtocol();
    }
    if (group != null && group.getBody() != null) {
      return group.getBody().getProtocol();
    }
    return null;
  }

  /**
   * 获取字节长度
   */
  public int getByteLength() {
    if (lengthUnit == LengthUnit.BYTE) {
      return evaluateLength();
    }
    return (evaluateLength() + 7) / 8;
  }

  /**
   * 获取位长度
   */
  public int getBitLength() {
    if (lengthUnit == LengthUnit.BIT) {
      return evaluateLength();
    }
    return evaluateLength() * 8;
  }

  /**
   * 计算实际长度值
   */
  private int evaluateLength() {
    try {
      if (length == null || length.trim().isEmpty()) {
        return 0;
      }

      // 如果是纯数字
      if (length.matches("\\d+")) {
        return Integer.parseInt(length);
      }

      // 如果包含表达式，使用表达式计算器
      Expression expression = ExpressionFactory.getExpression(length);
      Object     result     = expression.evaluate(null, new ExpressionContext(this));
      
      if (result instanceof Number) {
        return ((Number) result).intValue();
      }
      
      throw new ProtocolException("Invalid length expression result: " + result);
    } catch (Exception e) {
      System.out.println("Error evaluating length expression: " + length);
    }
    return 0;
  }
} 