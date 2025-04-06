package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.model.enums.LengthUnit;
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
    /** 组件路径 */
    protected final String     componentPath;
    /** 组件长度单位 */
    protected final LengthUnit lengthUnit;
    
    protected AbstractProtocolComponent(String componentPath, LengthUnit lengthUnit) {
        this.componentPath = componentPath;
        this.lengthUnit = lengthUnit;
    }
    
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
    
    @Override
    public void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException {
        // 保存原来的路径
        String previousPath = context.getCurrentPath();
        // 设置当前路径
        context.setCurrentPath(componentPath);
        
        log.info("开始编码组件: {}", componentPath);
        int startIndex = buffer.writerIndex();
        
        // 编码所有子组件
        for (ProtocolComponent child : children) {
            child.encode(buffer, context);
        }
        
        // 记录组件的数据范围
        int length = buffer.writerIndex() - startIndex;
        context.recordComponentRange(componentPath, startIndex, length, lengthUnit);
        
        // 将组件的完整数据存入上下文
        byte[] data = new byte[length];
        buffer.getBytes(startIndex, data);
        context.putNodeValue(componentPath, data);
        
        log.info("完成组件编码: {}, 长度: {} {}", componentPath, length, lengthUnit.name());
        
        // 恢复原来的路径
        context.setCurrentPath(previousPath);
    }
}