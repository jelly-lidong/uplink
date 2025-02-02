import com.alibaba.fastjson.JSONObject;
import com.example.protocol.model.Protocol;
import com.example.protocol.parse.JaxbProtocolParser;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class JaxbProtocolParserTest {

  public static void main(String[] args) throws IOException {
    String             xml      = FileUtils.readFileToString(new File("E:\\worksapce\\project\\uplink\\common-coder\\src\\test\\resources\\proto.xml"), "UTF-8");
    JaxbProtocolParser parser   = new JaxbProtocolParser();
    Protocol           protocol = parser.toJavaObject(xml);
    System.out.println(JSONObject.toJSON(protocol).toString());
  }

}   
