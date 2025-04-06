package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.model.ProtocolBody;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * 协议体组件 - 处理协议主体部分
 * 负责处理协议体中的所有子组件,包括子协议头、节点、节点组、子协议体和校验
 */
@Slf4j
public class BodyComposite extends AbstractProtocolComponent {

  /** 协议体对象 */
  private final ProtocolBody body;

  /**
   * 构造函数
   * @param body 协议体对象
   * @param parentPath 父组件路径
   */
  public BodyComposite(ProtocolBody body, String parentPath) {
    super(parentPath + "/body", body.getLengthUnit());
    this.body = body;
    log.info("创建协议体组件");
    initializeChildren();
  }

  /**
   * 初始化子组件
   * 按照协议体的结构创建各种子组件
   */
  private void initializeChildren() {
    // 处理子协议头
    if (body.getHeader() != null) {
      log.info("添加协议体子头部组件");
      addChild(new HeaderComposite(body.getHeader(), componentPath));
    }

    // 处理节点
    if (body.getNodes() != null) {
      log.info("添加协议体节点, 数量: {}", body.getNodes().size());
      body.getNodes().forEach(node ->
          addChild(new NodeComponent(node, componentPath)));
    }

    // 处理节点组
    if (body.getNodeGroups() != null) {
      log.info("添加协议体节点组, 数量: {}", body.getNodeGroups().size());
      body.getNodeGroups().forEach(group ->
          addChild(new NodeGroupComponent(group, componentPath)));
    }

    // 处理子协议体
    if (body.getSubBody() != null) {
      log.info("添加子协议体组件");
      addChild(new BodyComposite(body.getSubBody(), componentPath));
    }

    // 处理校验
    if (body.getCheck() != null) {
      log.info("添加协议体校验组件");
      addChild(new CheckComposite(body.getCheck(), componentPath));
    }
  }

  @Override
  public void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
    log.info("开始编码协议体");
    int startIndex = buffer.writerIndex();
    
    for (ProtocolComponent child : children) {
        child.encode(buffer, context);
    }
    
    int length = buffer.writerIndex() - startIndex;
    context.recordComponentRange(componentPath, startIndex, length, lengthUnit);
    log.info("完成协议体编码, 长度: {}", length);
  }

  @Override
  public void decode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
    log.info("开始解码协议体");
    for (ProtocolComponent child : children) {
      child.decode(buffer, context);
    }
    log.info("完成协议体解码");
  }
} 