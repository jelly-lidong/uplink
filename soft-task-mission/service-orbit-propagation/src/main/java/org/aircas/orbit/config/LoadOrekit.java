package org.aircas.orbit.config;


import java.io.File;
import java.util.Locale;
import org.orekit.data.DataContext;
import org.orekit.data.DirectoryCrawler;

public class LoadOrekit {

  static {
    // 配置参考上下文
    final File orekitData = new File("/orekit-data");
    if (!orekitData.exists()) {
      System.err.format(Locale.US, "无法找到 %s 文件夹%n",
          orekitData.getAbsolutePath());
      System.err.format(Locale.US, "您需要从 %s 下载 %s，解压缩到 %s 并将其重命名为 'orekit-data'，以使本教程正常工作%n",
          "https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip",
          "orekit-data-master.zip",
          orekitData.getAbsolutePath());
      System.exit(1);
    }
    DataContext.
        getDefault().
        getDataProvidersManager().
        addProvider(new DirectoryCrawler(orekitData));
  }
}
