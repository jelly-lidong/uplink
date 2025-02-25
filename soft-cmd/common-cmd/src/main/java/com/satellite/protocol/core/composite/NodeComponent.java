package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.core.handler.NodeHandler;
import com.satellite.protocol.core.handler.NodeHandlerFactory;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import io.netty.buffer.ByteBuf;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 节点组件 - 叶子节点
 * 负责单个节点的编解码,是组合模式中的叶子节点
 */
@Slf4j
public class NodeComponent implements ProtocolComponent {

  /** 节点对象 */
  private final Node               node;
  /** 节点处理器工厂 */
  private final NodeHandlerFactory handlerFactory;
  private final String             nodePath; // 添加节点路径字段

  /**
   * 构造函数
   * @param node 要处理的节点对象
   * @param parentPath 父节点路径
   */
  public NodeComponent(Node node, String parentPath) {
    this.node           = node;
    this.handlerFactory = NodeHandlerFactory.getInstance();
    this.nodePath       = parentPath + "/" + node.getName();
    log.debug("创建节点组件: {}, 路径: {}", node.getName(), nodePath);
  }

  @Override
  public void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
    log.debug("开始编码节点: {}, 类型: {}, 路径: {}",
        node.getName(), node.getType(), nodePath);

    // 检查是否有依赖
    if (hasDependencies(node)) {
      if (areDependenciesSatisfied(node, context)) {
        context.addPendingNode(node, getDependencyPaths(node, context));
        log.debug("节点 {} 依赖未满足，加入待处理队列", nodePath);
        return;
      }
    }

    // 处理节点
    NodeHandler handler = handlerFactory.getHandler(node);
    Object      value   = node.getValue();

    // 如果有转换表达式，先计算转换结果
    String convert = node.getConvert();
    if (convert != null && !convert.isEmpty()) {
      if (node.getType() == NodeType.TIMESTAMP) {
        node.setValue("'" + value + "'"); // 保存原始值，用于后续计算
      }
      convert = convert.replace("x", node.getValue()).replace("X", node.getValue());
      value   = context.evaluateExpression(convert, getDependencyPaths(node, context));
      log.debug("转换表达式计算结果: {} -> {}", convert, value);
    }

    // 转换值类型并编码
    value = convertNodeValue(value, node.getType());
    byte[] bytes = handler.encode(node, value);

    // 写入编码后的数据
    buffer.writeBytes(bytes);

    // 将节点值存入上下文
    if (value != null) {
      context.putNodeValue(nodePath, value);
      log.debug("完成节点编码: {}, 值: {}", nodePath, value);
    } else {
      log.warn("节点 {} 的值为null，跳过存储", nodePath);
    }
  }

  /**
   * 解析枚举值字符串为整数
   * @param enumValue 枚举值字符串
   * @return 解析后的整数值
   */
  private int parseEnumValue(String enumValue) {
    String value = enumValue.trim();
    try {
      if (value.startsWith("0x") || value.startsWith("0X")) {
        // 十六进制
        return Integer.parseInt(value.substring(2), 16);
      } else if (value.startsWith("0b") || value.startsWith("0B")) {
        // 二进制
        return Integer.parseInt(value.substring(2), 2);
      } else if (value.startsWith("0")) {
        // 八进制
        return Integer.parseInt(value, 8);
      } else {
        // 十进制
        return Integer.parseInt(value);
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          String.format("无效的枚举值格式: %s", value));
    }
  }

  /**
   * 根据节点类型转换值为合适的类型
   * @param value 原始值
   * @param type 节点类型
   * @return 转换后的值
   */
  private Object convertNodeValue(Object value, NodeType type) {
    if (value == null) {
      return null;
    }

    String strValue = value.toString();
    try {
      switch (type) {
        case BIT:
          if (strValue.startsWith("0b")) {
            // 将二进制字符串转换为整数
            return Integer.parseInt(strValue.substring(2), 2);
          }
          return Integer.parseInt(strValue);
        case INT:
          return Integer.parseInt(strValue);
        case FLOAT:
          return Float.parseFloat(strValue);
        case DOUBLE:
          return Double.parseDouble(strValue);
        case STRING:
          return strValue;
        case HEX:
          if (strValue.startsWith("0x") || strValue.startsWith("0X")) {
            // 将十六进制字符串转换为整数
            return Integer.parseInt(strValue.substring(2), 16);
          }
          return Integer.parseInt(strValue);
        case ENUM:
          // 处理枚举值
          if (node.getEnumValues() != null) {
            // 查找匹配的枚举值定义
            return node.getEnumValues().stream()
                .filter(enumValue -> strValue.equals(enumValue.getName()))
                .findFirst()
                .map(enumValue -> {
                  String enumStrValue = enumValue.getValue();
                  int    enumIntValue = parseEnumValue(enumStrValue);
                  log.debug("枚举值转换: {} -> {} ({})",
                      strValue, enumStrValue, enumIntValue);
                  return enumIntValue;
                })
                .orElseThrow(() -> new IllegalArgumentException(
                    String.format("未找到枚举值定义: %s", strValue)));
          }
          // 如果没有枚举值定义，尝试直接解析为整数
          return parseEnumValue(strValue);
//        case TIMESTAMP:
//                    return parseTimestamp(strValue);

        default:
          return value;
      }
    } catch (NumberFormatException | DateTimeParseException e) {
      log.error("转换节点值失败: {}, 类型: {}, 值: {}", node.getName(), type, strValue);
      throw new IllegalArgumentException(
          String.format("无效的节点值格式: %s (节点: %s, 类型: %s)",
              strValue, node.getName(), type));
    }
  }

  /**
   * 解析时间戳字符串
   * @param timestampStr 时间戳字符串
   * @return Unix时间戳（毫秒）
   */
  private long parseTimestamp(String timestampStr) {
    try {
      // 首先尝试解析为数字（Unix时间戳）
      return Long.parseLong(timestampStr);
    } catch (NumberFormatException e) {
      // 如果不是数字，尝试解析为日期时间字符串
      try {
        // 支持多种日期时间格式
        String[] patterns = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss",
            "yyyyMMddHHmmss"
        };

        for (String pattern : patterns) {
          try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime     dateTime  = LocalDateTime.parse(timestampStr, formatter);
            // 转换为Unix时间戳（毫秒）
            long timestamp = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            log.debug("时间戳转换: {} -> {} (使用格式: {})",
                timestampStr, timestamp, pattern);
            return timestamp;
          } catch (DateTimeParseException ignored) {
            // 继续尝试下一个格式
          }
        }

        // 如果所有格式都无法解析，抛出异常
        throw new IllegalArgumentException(
            String.format("不支持的时间格式: %s", timestampStr));
      } catch (Exception ex) {
        log.error("解析时间戳失败: {}", timestampStr, ex);
        throw new IllegalArgumentException(
            String.format("时间戳解析失败: %s", timestampStr));
      }
    }
  }

  @Override
  public void decode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
    log.debug("开始解码节点: {}, 类型: {}, 路径: {}",
        node.getName(), node.getType(), nodePath);

    // 检查是否有依赖
    if (hasDependencies(node)) {
      // 如果有依赖且依赖未满足，则加入待处理队列
      if (areDependenciesSatisfied(node, context)) {
        context.addPendingNode(node, getDependencyPaths(node, context));
        log.debug("节点 {} 依赖未满足，加入待处理队列", nodePath);
        return;
      }
    }

    // 处理节点
    NodeHandler handler = handlerFactory.getHandler(node);
    byte[]      bytes   = new byte[node.getByteLength()];
    buffer.readBytes(bytes);
    Object value = handler.decode(node, bytes, 0);
    node.setValue(value.toString());

    // 将节点值存入上下文
    context.putNodeValue(nodePath, value);
    log.debug("完成节点解码: {}, 值: {}", nodePath, value);
  }

  /**
   * 检查节点是否有依赖
   * 通过检查节点的refValue属性判断是否引用了其他节点的值
   */
  private boolean hasDependencies(Node node) {
    String refValue = node.getRefValue();
    // 如果refValue不为空且包含${}格式的节点引用，说明该节点依赖于其他节点
    return refValue != null && refValue.contains("${");
  }

  /**
   * 检查节点的依赖是否都满足
   * 根据refValue中引用的节点路径检查依赖节点的值是否存在
   */
  private boolean areDependenciesSatisfied(Node node, ProtocolContext context) {
    String   refValue = node.getRefValue();
    String[] nodeRefs = parseNodeRefs(refValue);
    for (String nodeRef : nodeRefs) {
      if (context.getNodeValue(nodeRef) == null) {
        return true; // 依赖未满足，需要加入待处理队列
      }
    }
    return false; // 所有依赖都满足，可以直接处理
  }

  /**
   * 解析refValue中引用的节点路径
   * @param refValue 引用值表达式
   * @return 依赖的节点路径数组
   */
  private String[] parseNodeRefs(String refValue) {
    if (refValue == null || refValue.trim().isEmpty()) {
      return new String[0];
    }

    Set<String> nodeRefs = new HashSet<>();
    Pattern     pattern  = Pattern.compile("\\$\\{([^}]+)}");
    Matcher     matcher  = pattern.matcher(refValue);

    while (matcher.find()) {
      String nodePath = matcher.group(1).trim();
      // 验证路径格式
      if (nodePath.isEmpty()) {
        throw new IllegalArgumentException("节点引用路径不能为空");
      }
      nodeRefs.add(nodePath);
      log.debug("找到节点引用路径: {}", nodePath);
    }

    log.debug("解析节点引用: {} -> {}", refValue, nodeRefs);
    return nodeRefs.toArray(new String[0]);
  }

  /**
   * 获取依赖节点的路径列表
   */
  private String[] getDependencyKeys(Node node) {
    String refValue = node.getRefValue();
    return parseNodeRefs(refValue);
  }

  private String[] getDependencyPaths(Node node, ProtocolContext context) {
    String[] relativePaths = parseNodeRefs(node.getRefValue());
    String[] absolutePaths = new String[relativePaths.length];

    for (int i = 0; i < relativePaths.length; i++) {
      absolutePaths[i] = context.resolveNodePath(nodePath, relativePaths[i]);
    }

    return absolutePaths;
  }

  // 叶子节点不支持子节点操作
  @Override
  public void addChild(ProtocolComponent child) {
    throw new UnsupportedOperationException("Leaf node doesn't support children");
  }

  @Override
  public void removeChild(ProtocolComponent child) {
    throw new UnsupportedOperationException("Leaf node doesn't support children");
  }

  @Override
  public ProtocolComponent getChild(int index) {
    throw new UnsupportedOperationException("Leaf node doesn't support children");
  }
} 