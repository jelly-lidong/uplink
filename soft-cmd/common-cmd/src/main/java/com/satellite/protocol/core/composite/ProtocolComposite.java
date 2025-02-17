package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.model.Protocol;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * 协议复合组件 - 处理整个协议
 * 作为协议树的根节点,负责协调整个协议的编解码过程
 */
@Slf4j
public class ProtocolComposite extends AbstractProtocolComponent {

  /** 协议对象,包含完整的协议定义 */
  private final Protocol protocol;

  /**
   * 构造函数
   * @param protocol 要处理的协议对象
   */
  public ProtocolComposite(Protocol protocol) {
    super("/protocol/" + protocol.getName(), protocol.getLengthUnit());
    this.protocol = protocol;
    log.debug("创建协议组件: {}", protocol.getName());
    initializeChildren();
  }

  /**
   * 初始化子组件
   * 根据协议结构创建对应的头部、主体和校验组件
   */
  private void initializeChildren() {
    if (protocol.getHeader() != null) {
      log.debug("添加协议头组件");
      addChild(new HeaderComposite(protocol.getHeader(), componentPath));
    }
    if (protocol.getBody() != null) {
      log.debug("添加协议体组件");
      addChild(new BodyComposite(protocol.getBody(), componentPath));
    }
    if (protocol.getCheck() != null) {
      log.debug("添加协议校验组件");
      addChild(new CheckComposite(protocol.getCheck(), componentPath));
    }
  }

  @Override
  public void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
    log.info("开始编码协议: {}", protocol.getName());
    for (ProtocolComponent child : children) {
      child.encode(buffer, context);
    }
    log.info("完成协议编码: {}", protocol.getName());
  }

  @Override
  public void decode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
    log.info("开始解码协议: {}", protocol.getName());
    for (ProtocolComponent child : children) {
      child.decode(buffer, context);
    }
    log.info("完成协议解码: {}", protocol.getName());
  }
} 