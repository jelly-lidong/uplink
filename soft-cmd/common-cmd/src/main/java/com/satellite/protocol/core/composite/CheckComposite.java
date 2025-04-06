package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.model.ProtocolCheck;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * 协议校验组件 - 处理协议校验部分
 * 负责处理协议的校验节点,通常包含校验和等信息
 */
@Slf4j
public class CheckComposite extends AbstractProtocolComponent {
    /** 协议校验对象 */
    private final ProtocolCheck check;

    /**
     * 构造函数
     * @param check 协议校验对象
     * @param parentPath 父路径
     */
    public CheckComposite(ProtocolCheck check, String parentPath) {
        super(parentPath + "/check", check.getLengthUnit());
        this.check = check;
        log.info("创建协议校验组件");
        initializeChildren();
    }
    
    /**
     * 初始化子组件
     * 为校验部分的每个节点创建对应的节点组件
     */
    private void initializeChildren() {
        if (check.getNodes() != null) {
            log.info("添加校验节点, 数量: {}", check.getNodes().size());
            check.getNodes().forEach(node -> 
                addChild(new NodeComponent(node, componentPath)));
        }
    }
    
    @Override
    public void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        log.info("开始编码协议校验");
        for (ProtocolComponent child : children) {
            child.encode(buffer, context);
        }
        log.info("完成协议校验编码");
    }
    
    @Override
    public void decode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        log.info("开始解码协议校验");
        for (ProtocolComponent child : children) {
            child.decode(buffer, context);
        }
        log.info("完成协议校验解码");
    }
} 