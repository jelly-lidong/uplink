package org.aircas.orbit.init;

import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.util.OrbitUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppRunner implements ApplicationRunner {

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("AppRunner....");
    OrbitUtil.initOrekit();
  }


}
