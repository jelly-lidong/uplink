package org.aircas.resource.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext初始化或刷新完成后触发的事件；也就是容器初始化完成后调用。
 */
@Slf4j
@Component
public class ApplicationRefreshed implements ApplicationListener<ContextRefreshedEvent> {


  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    log.info("application refreshed...");
    log.info("user.home:{}", System.getProperty("user.home"));

  }
}
