package com.satellite.protocol.core.composite;

import com.satellite.protocol.core.ProtocolException;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import com.satellite.protocol.core.context.ProtocolContext;

/**
 * 协议组件接口 - 组合模式的基本组件
 * 定义了协议组件的基本操作,包括编码、解码和子组件管理
 */
public interface ProtocolComponent {
    /**
     * 将组件编码为字节流
     * @param buffer 用于存储编码后的字节数据
     * @param context 协议上下文
     * @throws ProtocolException 编码过程中的异常
     */
    void encode(ByteBuf buffer, ProtocolContext context) throws ProtocolException;

    /**
     * 从字节流解码组件数据
     * @param buffer 包含待解码数据的字节缓冲区
     * @param context 协议上下文
     * @throws ProtocolException 解码过程中的异常
     */
    void decode(ByteBuf buffer, ProtocolContext context) throws ProtocolException;

    /**
     * 添加子组件
     * @param child 要添加的子组件
     */
    void addChild(ProtocolComponent child);

    /**
     * 移除子组件
     * @param child 要移除的子组件
     */
    void removeChild(ProtocolComponent child);

    /**
     * 获取指定索引的子组件
     * @param index 子组件索引
     * @return 对应索引的子组件
     */
    ProtocolComponent getChild(int index);
} 