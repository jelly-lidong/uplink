package com.satellite.protocol.core.function;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.handler.impl.DoubleNodeHandler;
import com.satellite.protocol.core.handler.impl.FloatNodeHandler;
import com.satellite.protocol.core.handler.impl.IntNodeHandler;
import com.satellite.protocol.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;

public abstract class AbstractCheckFunction implements Function {
    
    protected byte[] getNodeBytes(Node node) throws ProtocolException {
        if (node.getValue() == null) {
            throw new ProtocolException("Node value is null: " + node.getName());
        }
        
        // 使用节点对应的处理器将值转换为字节数组
        AbstractNodeHandler handler = getHandlerForNode(node);
        return handler.encode(node, node.getValue());
    }
    
    protected byte[] getBodyBytes(ProtocolBody body) throws ProtocolException {
        ByteBuf buffer = Unpooled.buffer();
        try {
            // 处理header
            if (body.getHeader() != null) {
                processBodyPart(body.getHeader().getNodes(), buffer);
            }
            
            // 处理nodes
            if (body.getNodes() != null) {
                processBodyPart(body.getNodes(), buffer);
            }
            
            // 处理nodeGroups
            if (body.getNodeGroups() != null) {
                for (NodeGroup group : body.getNodeGroups()) {
                    processBodyPart(group.getNodes(), buffer);
                }
            }
            
            // 处理subBody
            if (body.getSubBody() != null) {
                byte[] subBodyBytes = getBodyBytes(body.getSubBody());
                buffer.writeBytes(subBodyBytes);
            }
            
            byte[] result = new byte[buffer.readableBytes()];
            buffer.readBytes(result);
            return result;
        } finally {
            buffer.release();
        }
    }
    
    private void processBodyPart(List<Node> nodes, ByteBuf buffer) throws ProtocolException {
        if (nodes != null) {
            for (Node node : nodes) {
                byte[] nodeBytes = getNodeBytes(node);
                buffer.writeBytes(nodeBytes);
            }
        }
    }
    
    private AbstractNodeHandler getHandlerForNode(Node node) {
        // 这里需要根据节点类型返回对应的处理器
        // 可以注入DefaultProtocolProcessor中的handlers，或者重新创建处理器
        switch (node.getType()) {
            case INT:
                return new IntNodeHandler();
            case FLOAT:
                return new FloatNodeHandler();
            case DOUBLE:
                return new DoubleNodeHandler();
            // ... 其他类型
            default:
                throw new IllegalArgumentException("Unsupported node type: " + node.getType());
        }
    }
} 