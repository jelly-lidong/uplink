package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import java.nio.ByteBuffer;

/**
 * 浮点数节点处理器
 */
public class FloatNodeHandler extends AbstractNodeHandler {
    
    public FloatNodeHandler() {
        super(NodeType.FLOAT);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        float floatValue;
        if (value instanceof Number) {
            floatValue = ((Number) value).floatValue();
        } else {
            throw new ProtocolException("Invalid value type for float node: " + value.getClass());
        }
        
        return ByteBuffer.allocate(4).putFloat(floatValue).array();
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        return ByteBuffer.wrap(bytes, offset, 4).getFloat();
    }
} 