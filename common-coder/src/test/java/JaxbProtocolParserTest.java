import com.alibaba.fastjson.JSONObject;
import com.example.protocol.model.Protocol;
import com.example.protocol.parse.JaxbProtocolParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

@RunWith(JUnit4.class)
public class JaxbProtocolParserTest {

    @Test
    public void toJavaObject() throws IOException {
        String xml = FileUtils.readFileToString(new File("/Users/lidong/workspace/my-project/study/spring-cloud-alibaba-lidong/cmd-coder/src/test/resources/proto.xml"), "UTF-8");
        JaxbProtocolParser parser = new JaxbProtocolParser();
        Protocol protocol = parser.toJavaObject(xml);
        System.out.println(JSONObject.toJSON(protocol).toString());
    }

    @Test
    public void parseObject() {

    }
}   
