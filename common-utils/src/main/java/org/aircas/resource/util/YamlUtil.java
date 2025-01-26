package org.aircas.resource.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class YamlUtil {

  /**
   * 从yaml文件加载对象属性
   */
  public static <T> T readObjFromYaml(ClassLoader classLoader, String path, Class<T> tClass) throws URISyntaxException, IOException {
    File file = new File(Objects.requireNonNull(classLoader.getResource(path)).toURI().getPath());
    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    return mapper.readValue(file, tClass);
  }
}
