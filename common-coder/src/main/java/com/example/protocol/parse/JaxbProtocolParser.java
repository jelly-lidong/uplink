package com.example.protocol.parse;

import com.example.protocol.model.Protocol;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * JAXB 协议解析器，实现 ProtocolParser 接口。
 */
public class JaxbProtocolParser implements ProtocolParser {

    @Override
    public Protocol toJavaObject(String xml) {
        try {
            JAXBContext context = JAXBContext.newInstance(Protocol.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Protocol) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String parseObject(Protocol protocol) {
        try {
            JAXBContext context = JAXBContext.newInstance(Protocol.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(protocol, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
} 