package org.aircas.orbit.example.quartz;

import org.apache.commons.lang3.BitField;

public class BitFieldExample {
  public static void main(String[] args) {
    // 创建一个 BitField 对象，定义一个位域，从第2位开始，长度为3位
    
    BitField bitField = new BitField(0b111 << 2);

    // 初始化一个整数值
    int value = 0;

    // 设置位域的值
    value = bitField.setValue(value, 0b101);
    System.out.println("设置后的值: " + Integer.toBinaryString(value));

    // 获取位域的值
    int bitFieldValue = bitField.getValue(value);
    System.out.println("位域的值: " + Integer.toBinaryString(bitFieldValue));

    // 清除位域的值
    value = bitField.clear(value);
    System.out.println("清除后的值: " + Integer.toBinaryString(value));
  }
}
