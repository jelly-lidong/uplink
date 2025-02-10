package org.aircas.orbit.util.file.xml.adapter;

import java.text.NumberFormat;
import java.util.Locale;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 格式化数字 如果不生效就放在get方法上
 */
public class JaxbNumberAdapter extends XmlAdapter<String, Number> {

  @Override
  public Number unmarshal(String v) throws Exception {
    if (v == null) {
      return null;
    }
    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
    return format.parse(v);
  }

  @Override
  public String marshal(Number v) throws Exception {
    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
    return format.format(v);
  }
}