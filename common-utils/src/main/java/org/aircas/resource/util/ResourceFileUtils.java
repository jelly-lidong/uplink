package org.aircas.resource.util;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

public class ResourceFileUtils {

  /**
   * 读取resource目录下的文件
   */
  public static File readResourceFile(ClassLoader classLoader, String relativePath) throws URISyntaxException {
    return new File(Objects.requireNonNull(classLoader.getResource(relativePath)).toURI().getPath());
  }

}
