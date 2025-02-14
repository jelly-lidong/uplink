package com.satellite.protocol.core.composite;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import io.netty.buffer.ByteBuf;

/**
 * 抽象协议组件 - 提供组件接口的基础实现
 * 实现了子组件管理的通用逻辑,具体的编解码逻辑由子类实现
 */
@Slf4j
public abstract class AbstractProtocolComponent implements ProtocolComponent {
    /** 存储子组件的列表 */
    protected final List<ProtocolComponent> children = new ArrayList<>();
    
    @Override
    public void addChild(ProtocolComponent child) {
        if (child != null) {
            children.add(child);
        }
    }
    
    @Override
    public void removeChild(ProtocolComponent child) {
        children.remove(child);
    }
    
    @Override
    public ProtocolComponent getChild(int index) {
        return children.get(index);
    }
}