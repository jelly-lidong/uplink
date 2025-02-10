package org.aircas.orbit.util.file.xml.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 格式化时间 如果不生效就放在get方法上
 */
public class JaxbDateAdapter extends XmlAdapter<String, Date> {

  private static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Override
  public Date unmarshal(String v) throws Exception {
    if (v == null) {
      return null;
    }

    DateFormat format = new SimpleDateFormat(STANDARD_DATE_FORMAT);
    return format.parse(v);
  }

  @Override
  public String marshal(Date v) throws Exception {
    DateFormat format = new SimpleDateFormat(STANDARD_DATE_FORMAT);
    return format.format(v);
  }
}