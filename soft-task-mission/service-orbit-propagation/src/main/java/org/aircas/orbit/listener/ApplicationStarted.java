package org.aircas.orbit.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext启动后触发的事件
 */
@Component
@Slf4j
public class ApplicationStarted implements ApplicationListener<ContextStartedEvent> {

  @Override
  public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {

    log.info("application started...");
  }
}
