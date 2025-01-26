package org.aircas.resource.util;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;

@Slf4j
public class OrbitUtil {

  /**
   * 读取orekit数据
   */
  public static void initOrekit() {
    final File home = new File(System.getProperty("user.home"));
    final File orekitData = new File(home, "orekit-data");
    if (!orekitData.exists()) {
      log.error("Failed to find folder: {}", orekitData.getAbsolutePath());
      System.exit(1);
    }
    final DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
    manager.addProvider(new DirectoryCrawler(orekitData));
    log.warn("成功加载Orekit...");
  }
}
