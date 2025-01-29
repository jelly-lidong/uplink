package org.aircas.orbit.file.xml;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XMLUtil {

  /**
   * JavaBean转换成xml
   */
  public static String convertToXml(Object obj, String encoding) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(obj.getClass());
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

    StringWriter writer = new StringWriter();
    marshaller.marshal(obj, writer);
    return writer.toString();
  }

  /**
   * xml转换成JavaBean
   */
  @SuppressWarnings("unchecked")
  public static <T> T convertToJavaBean(String xml, Class<T> c) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(c);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    return (T) unmarshaller.unmarshal(new StringReader(xml));
  }
}

