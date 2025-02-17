package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.model.ProtocolHeader;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * 协议头组件 - 处理协议头部分
 * 负责协议头中所有节点的编解码
 */
@Slf4j
public class HeaderComposite extends AbstractProtocolComponent {
    /** 协议头对象 */
    private final ProtocolHeader header;
    
    /**
     * 构造函数
     * @param header 协议头对象
     */
    public HeaderComposite(ProtocolHeader header, String parentPath) {
        super(parentPath + "/header", header.getLengthUnit());
        this.header = header;
        initializeChildren();
    }
    
    /**
     * 初始化子组件
     * 为协议头中的每个节点创建对应的节点组件
     */
    private void initializeChildren() {
        if (header.getNodes() != null) {
            log.debug("添加协议头节点, 数量: {}", header.getNodes().size());
            header.getNodes().forEach(node -> 
                addChild(new NodeComponent(node, componentPath)));
        }
    }
    
    @Override
    public void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        log.debug("开始编码协议头");
        for (ProtocolComponent child : children) {
            child.encode(buffer, context);
        }
        log.debug("完成协议头编码");
    }
    
    @Override
    public void decode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        log.debug("开始解码协议头");
        for (ProtocolComponent child : children) {
            child.decode(buffer, context);
        }
        log.debug("完成协议头解码");
    }
} 