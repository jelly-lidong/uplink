package com.satellite.protocol;

import com.alibaba.fastjson.JSONObject;
import com.satellite.protocol.core.CmdProtocolCodec;
import com.satellite.protocol.core.ProtocolCodec;
import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.model.Protocol;
import com.satellite.protocol.util.ProtocolXmlParser;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class MainApp {

  public static void main(String[] args) throws ProtocolException {
    // 测试解析
    String   xmlPath  = "E:\\worksapce\\project\\uplink\\soft-cmd\\common-cmd\\protocol.xml"; // 确保路径正确
    Protocol protocol = ProtocolXmlParser.parseProtocolXml(xmlPath);
    System.out.println("协议对象: " + JSONObject.toJSONString(protocol));
    ProtocolCodec codec = new CmdProtocolCodec();
    byte[]        bytes = codec.encode(protocol);
    System.out.println(HexBin.encode(bytes));
  }
}
