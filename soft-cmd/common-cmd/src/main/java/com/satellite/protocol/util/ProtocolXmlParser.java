package com.satellite.protocol.util;

import com.alibaba.fastjson.JSONObject;
import com.satellite.protocol.model.Protocol;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class ProtocolXmlParser {
    
    public static Protocol parseProtocolXml(String xmlPath) {
        try {
            JAXBContext context = JAXBContext.newInstance(Protocol.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            File xmlFile = new File(xmlPath);
            return (Protocol) unmarshaller.unmarshal(xmlFile);
        } catch (Exception e) {
            throw new RuntimeException("解析协议XML文件失败: " + e.getMessage(), e);
        }
    }
    
    public static void main(String[] args) {
        // 测试解析
        String xmlPath = "E:\\worksapce\\project\\uplink\\common-cmd\\protocol.xml"; // 确保路径正确
        Protocol protocol = parseProtocolXml(xmlPath);
        System.out.println("协议对象: " + JSONObject.toJSONString(protocol));
        
        // 转换为字节码
        byte[] binaryData = ProtocolBinaryConverter.convertToBinary(protocol);
        System.out.println("字节码长度: " + binaryData.length);
        System.out.println("字节码内容: " + bytesToHex(binaryData));
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
} 