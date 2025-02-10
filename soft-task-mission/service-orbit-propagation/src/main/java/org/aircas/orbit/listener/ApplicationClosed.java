package org.aircas.orbit.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext关闭后触发的事件； 如web容器关闭时自动会触发spring容器的关闭。甚至大家听说过的钩子程序都是调用ctx.registerShutdownHook()进行注册虚拟机关闭。
 */
@Component
@Slf4j
public class ApplicationClosed implements ApplicationListener<ContextClosedEvent> {

  @Override
  public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
    log.info("application closed....");
  }
}
