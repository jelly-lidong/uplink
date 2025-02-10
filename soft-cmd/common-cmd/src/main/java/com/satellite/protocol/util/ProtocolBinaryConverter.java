package com.satellite.protocol.util;

import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.Protocol;
import com.satellite.protocol.model.ProtocolBody;
import com.satellite.protocol.model.ProtocolCheck;
import com.satellite.protocol.model.ProtocolHeader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.math.BigInteger;

public class ProtocolBinaryConverter {

  private static Protocol currentProtocol;

  public static byte[] convertToBinary(Protocol protocol) {
    try {
      currentProtocol = protocol;
      CheckRangeParser.clearCache();
      
      List<byte[]> bytesList = new ArrayList<>();

      // 处理一级协议头
      byte[] headerBytes = processHeader(protocol.getHeader());
      bytesList.add(headerBytes);

      // 处理一级协议体
      byte[] bodyBytes = processBody(protocol.getBody());
      bytesList.add(bodyBytes);

      // 处理一级协议校验
      byte[] checkBytes = processCheck(protocol.getCheck());
      bytesList.add(checkBytes);

      // 合并所有字节数组
      return mergeBytes(bytesList);
    } finally {
      currentProtocol = null;
      CheckRangeParser.clearCache();
    }
  }

  public static byte[] processHeader(ProtocolHeader header) {
    List<byte[]> headerBytes = new ArrayList<>();

    for (Node node : header.getNodes()) {
      byte[] nodeBytes = processNode(node);
      headerBytes.add(nodeBytes);
    }

    return mergeBytes(headerBytes);
  }

  private static byte[] processBody(ProtocolBody body) {
    List<byte[]> bodyBytes = new ArrayList<>();

    // 处理二级协议头
    if (body.getHeader() != null) {
      bodyBytes.add(processHeader(body.getHeader()));
    }

    // 处理二级协议体
    if (body.getSubBody() != null) {
      bodyBytes.add(processBody(body.getSubBody()));
    }

    // 处理二级协议校验
    if (body.getCheck() != null) {
      bodyBytes.add(processCheck(body.getCheck()));
    }

    // 计算当前协议体的长度
    int currentBodyLength = bodyBytes.stream().mapToInt(arr -> arr.length).sum();

    // 如果有长度计算公式，进行处理
//    if (body.getLengthUnit() != null && body.getLength() > 0) {
//      String lengthFormula = body.getLengthFormula(); // 假设你在ProtocolBody中有一个lengthFormula字段
//      if (lengthFormula != null) {
//        currentBodyLength = evaluateLengthFormula(lengthFormula);
//      }
//    }

    return mergeBytes(bodyBytes);
  }

  private static byte[] processCheck(ProtocolCheck check) {
    List<byte[]> checkBytes = new ArrayList<>();

    for (Node node : check.getNodes()) {
      byte[] nodeBytes = processNode(node);
      checkBytes.add(nodeBytes);
    }

    return mergeBytes(checkBytes);
  }

  public static byte[] processNode(Node node) {
    switch (node.getType()) {
      case HEX:
        return processHexNode(node);
      case INT:
        return processIntNode(node);
      case BIT:
        return processBitNode(node);
      case TIMESTAMP:
        return processTimestampNode(node);
      case ENUM:
        return processEnumNode(node);
      case DYNAMIC:
        return processDynamicNode(node);
      case CRC16:
        return processCrc16Node(node);
      case CRC32:
        return processCrc32Node(node);
      case PADDING:
        return processPaddingNode(node);
      default:
        throw new IllegalArgumentException("Unsupported node type: " + node.getType());
    }
  }

  private static byte[] processHexNode(Node node) {
    String hexValue = node.getDefValue().replace("0x", "");
    return hexStringToByteArray(hexValue);
  }

  private static byte[] processIntNode(Node node) {
    if (node.getDefValue() == null) {
        throw new IllegalArgumentException(node.getName() + " 的defValue不能为空");
    }

    String defValue = node.getDefValue().trim();
    BigInteger value;

    // 检查是否为二进制格式
    if (defValue.startsWith("0b")) {
        value = new BigInteger(defValue.substring(2), 2); // 解析二进制
    } else {
        value = new BigInteger(defValue); // 解析十进制
    }

    // 根据长度写入数据
    int length = node.getLength();
    ByteBuffer buffer = ByteBuffer.allocate(length);
    buffer.order(ByteOrder.BIG_ENDIAN);

    // 将BigInteger转换为字节数组
    byte[] byteArray = value.toByteArray();

    // 处理负数情况
    if (byteArray.length > length) {
        throw new IllegalArgumentException("值超出支持的长度: " + length);
    }

    // 填充高位0
    byte[] paddedArray = new byte[length];
    int start = length - byteArray.length;
    System.arraycopy(byteArray, 0, paddedArray, start, byteArray.length);

    buffer.put(paddedArray);
    return buffer.array();
  }

  private static byte[] processBitNode(Node node) {
    int    value      = Integer.parseInt(node.getDefValue().replace("0x", ""), 16);
    int    byteLength = (node.getLength() + 7) / 8; // 向上取整得到字节数
    byte[] result     = new byte[byteLength];

    for (int i = 0; i < node.getLength(); i++) {
      int byteIndex = i / 8;
      int bitIndex  = 7 - (i % 8);
      if ((value & (1 << (node.getLength() - 1 - i))) != 0) {
        result[byteIndex] |= (byte) (1 << bitIndex);
      }
    }

    return result;
  }

  private static byte[] processTimestampNode(Node node) {
    String convert   = node.getConvert();
    long   timestamp = System.currentTimeMillis();

    if (convert != null) {
      if (convert.contains("day(x - '2020-01-01 00:00:00')")) {
        // 计算距离2020-01-01的天数
        long baseTime = 1577808000000L; // 2020-01-01 00:00:00的毫秒时间戳
        long days     = (timestamp - baseTime) / (24 * 60 * 60 * 1000);

        if (convert.endsWith("-1")) {
          days -= 1;
        } else if (convert.endsWith("+1")) {
          days += 1;
        }

        return longToBytes(days, node.getLength());
      } else if (convert.contains("millSeconds(x - 'currentDate')")) {
        // 计算距离当前时间的毫秒数
        long milliseconds = 0; // 这里可以根据实际需求计算相对毫秒数

        if (convert.endsWith("-1")) {
          milliseconds -= 1;
        } else if (convert.endsWith("+1")) {
          milliseconds += 1;
        }

        return longToBytes(milliseconds, node.getLength());
      } else if (convert.contains("seconds(x - 'currentDate')")) {
        // 计算距离当前时间的秒数
        long seconds = 0; // 这里可以根据实际需求计算相对秒数

        if (convert.endsWith("-1")) {
          seconds -= 1;
        } else if (convert.endsWith("+1")) {
          seconds += 1;
        }

        return longToBytes(seconds, node.getLength());
      }
    }

    return longToBytes(timestamp, node.getLength());
  }

  private static byte[] processEnumNode(Node node) {
    // 实现枚举值转换逻辑
    return new byte[node.getLength()];
  }

  private static byte[] processDynamicNode(Node node) {
    String     refType = node.getRefValue(); // 获取引用的数据类型
    ByteBuffer buffer  = ByteBuffer.allocate(node.getLength());
    buffer.order(ByteOrder.LITTLE_ENDIAN); // 使用小端序

    if ("整型".equals(refType)) {
      int value = Integer.parseInt(node.getDefValue());
      buffer.putInt(value);
    } else if ("浮点型".equals(refType)) {
      float value = Float.parseFloat(node.getDefValue());
      if (node.getConvert() != null && node.getConvert().contains("*0.001")) {
        value *= 0.001F;
      }
      buffer.putFloat(value);
    } else if ("字符串".equals(refType)) {
      byte[] strBytes = node.getDefValue().getBytes();
      buffer.put(strBytes);
    }

    return buffer.array();
  }

  private static byte[] processCrc16Node(Node node) {
    String checkRange = node.getCheckRange();
    if (checkRange != null) {
      try {
        // 获取要校验的数据范围
        byte[] dataToCheck = CheckRangeParser.parseCheckRange(checkRange, currentProtocol);
        // 计算CRC16校验值
        return CrcUtil.calculateCRC16(dataToCheck);
      } catch (Exception e) {
        throw new RuntimeException("Failed to calculate CRC16: " + e.getMessage(), e);
      }
    }
    return new byte[2]; // 如果没有指定校验范围，返回默认值
  }

  private static byte[] processCrc32Node(Node node) {
    String checkRange = node.getCheckRange();
    if (checkRange != null) {
      try {
        // 获取要校验的数据范围
        byte[] dataToCheck = CheckRangeParser.parseCheckRange(checkRange, currentProtocol);
        // 计算CRC32校验值
        return CrcUtil.calculateCRC32(dataToCheck);
      } catch (Exception e) {
        throw new RuntimeException("Failed to calculate CRC32: " + e.getMessage(), e);
      }
    }
    return new byte[4]; // 如果没有指定校验范围，返回默认值
  }

  private static byte[] processPaddingNode(Node node) {
    byte[] padding = new byte[node.getLength()];
    // 填充指定的值
    if (node.getDefValue() != null) {
      byte paddingValue = Byte.parseByte(node.getDefValue().replace("0x", ""), 16);
      Arrays.fill(padding, paddingValue);
    }
    return padding;
  }

  private static byte[] mergeBytes(List<byte[]> byteArrays) {
    int        totalLength = byteArrays.stream().mapToInt(arr -> arr.length).sum();
    ByteBuffer buffer      = ByteBuffer.allocate(totalLength);
    byteArrays.forEach(buffer::put);
    return buffer.array();
  }

  private static byte[] hexStringToByteArray(String hex) {
    int    len  = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }

  private static byte[] longToBytes(long value, int length) {
    byte[] result = new byte[length];
    for (int i = 0; i < length; i++) {
      result[length - 1 - i] = (byte) (value & 0xFF);
      value >>= 8;
    }
    return result;
  }

  private int evaluateLengthFormula(String formula) {
    // 解析公式并计算长度
    // 例如: sum(../一级协议体.length) - 1
    // 这里需要实现具体的解析逻辑
    // 可以使用正则表达式或其他方法来提取和计算长度

    // 示例实现：简单处理sum和减法
    if (formula.startsWith("sum(") && formula.endsWith(")")) {
      String innerFormula = formula.substring(4, formula.length() - 1);
      // 处理内部公式
      // 这里需要实现具体的逻辑来查找对应的长度
      // 假设我们有一个方法可以获取长度
      int length = getLengthFromFormula(innerFormula);
      return length - 1; // 减去1
    }

    throw new IllegalArgumentException("不支持的长度公式: " + formula);
  }

  private int getLengthFromFormula(String formula) {
    // 解析内部公式，获取对应的长度
    // 这里需要实现具体的逻辑
    // 例如: "../一级协议体.length"
    // 你可以根据需要查找当前协议的父级协议体的长度
    return 0; // 返回计算后的长度
  }
} 