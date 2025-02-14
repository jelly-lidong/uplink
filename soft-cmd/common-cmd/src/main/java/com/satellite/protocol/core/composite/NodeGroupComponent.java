package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.model.NodeGroup;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * 节点组组件 - 处理节点组
 * 负责处理可重复的节点组,支持动态重复次数
 */
@Slf4j
public class NodeGroupComponent extends AbstractProtocolComponent {
    /** 节点组对象 */
    private final NodeGroup group;
    private final String componentPath; // 添加路径字段
    
    /**
     * 构造函数
     * @param group 节点组对象
     * @param parentPath 父路径
     */
    public NodeGroupComponent(NodeGroup group, String parentPath) {
        this.group = group;
        this.componentPath = parentPath + "/group/" + group.getName();
        log.debug("创建节点组组件: {}", group.getName());
        initializeChildren();
    }
    
    /**
     * 初始化子组件
     * 为节点组中的每个节点创建对应的节点组件
     */
    private void initializeChildren() {
        if (group.getNodes() != null) {
            log.debug("添加节点组中的节点, 组名: {}, 节点数量: {}", 
                group.getName(), group.getNodes().size());
            group.getNodes().forEach(node -> 
                addChild(new NodeComponent(node, componentPath)));
        }
    }
    
    @Override
    public void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        int repeat = evaluateRepeat();
        log.debug("开始编码节点组: {}, 重复次数: {}", group.getName(), repeat);
        for (int i = 0; i < repeat; i++) {
            log.debug("编码节点组第{}次重复", i + 1);
            for (ProtocolComponent child : children) {
                child.encode(buffer, context);
            }
        }
        log.debug("完成节点组编码: {}", group.getName());
    }
    
    @Override
    public void decode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        int repeat = evaluateRepeat();
        log.debug("开始解码节点组: {}, 重复次数: {}", group.getName(), repeat);
        for (int i = 0; i < repeat; i++) {
            log.debug("解码节点组第{}次重复", i + 1);
            for (ProtocolComponent child : children) {
                child.decode(buffer, context);
            }
        }
        log.debug("完成节点组解码: {}", group.getName());
    }
    
    /**
     * 计算节点组的重复次数
     * @return 重复次数
     */
    private int evaluateRepeat() {
        // TODO: 实现重复次数计算逻辑
        return 1;
    }
} 