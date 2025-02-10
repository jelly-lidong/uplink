package org.aircas.orbit.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext停止后触发的事件
 */
@Component
@Slf4j
public class ApplicationStopped implements ApplicationListener<ContextStoppedEvent> {

  @Override
  public void onApplicationEvent(ContextStoppedEvent contextStoppedEvent) {
    log.debug("application stopped...");
  }
}
