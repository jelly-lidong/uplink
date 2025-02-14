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

    }

} 