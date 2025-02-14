package com.satellite.protocol.core.handler.impl;

import com.satellite.protocol.core.handler.AbstractNodeHandler;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.enums.NodeType;
import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.expression.Expression;
import com.satellite.protocol.core.expression.ExpressionFactory;
import com.satellite.protocol.core.expression.ExpressionContext;

public class DynamicNodeHandler extends AbstractNodeHandler {
    
    public DynamicNodeHandler() {
        super(NodeType.DYNAMIC);
    }
    
    @Override
    public byte[] encode(Node node, Object value) throws ProtocolException {
        validateNode(node);
        if (value == null) {
            return new byte[node.getByteLength()];
        }
        
        // 动态节点的处理逻辑需要根据具体业务来实现
        throw new UnsupportedOperationException("Dynamic node handling not implemented");
    }
    
    @Override
    public Object decode(Node node, byte[] bytes, int offset) throws ProtocolException {
        validateNode(node);
        // 动态节点的处理逻辑需要根据具体业务来实现
        throw new UnsupportedOperationException("Dynamic node handling not implemented");
    }
    
    private NodeType determineActualType(Node node) throws ProtocolException {
        // 从父节点获取类型信息
        String parentPath = "../数据类型";  // 这里假设父节点中有个"数据类型"节点
        Expression refExpr = ExpressionFactory.getExpression("${" + parentPath + "}");
        Object typeValue = refExpr.evaluate(null, new ExpressionContext(node));
        
        if (typeValue == null) {
            throw new ProtocolException("Cannot determine actual type for dynamic node: " + node.getName());
        }
        
        // 根据类型值确定实际类型
        switch(typeValue.toString()) {
            case "整型":
                return NodeType.INT;
            case "浮点型":
                return NodeType.FLOAT;
            case "字符串":
                return NodeType.STRING;
            default:
                throw new ProtocolException("Unknown dynamic type: " + typeValue);
        }
    }
    
    private Object convertValue(Object value, Node node) throws ProtocolException {
        if (value == null) {
            return null;
        }
        
        NodeType actualType = determineActualType(node);
        try {
            switch (actualType) {
                case INT:
                    return value instanceof Number ? ((Number) value).intValue() 
                           : Integer.parseInt(value.toString());
                case FLOAT:
                    return value instanceof Number ? ((Number) value).floatValue()
                           : Float.parseFloat(value.toString());
                case STRING:
                    return value.toString();
                default:
                    throw new ProtocolException("Unsupported dynamic type: " + actualType);
            }
        } catch (NumberFormatException e) {
            throw new ProtocolException("Failed to convert value: " + value, e);
        }
    }
    
    private AbstractNodeHandler createHandler(NodeType type) {
        switch (type) {
            case INT:
                return new IntNodeHandler();
            case FLOAT:
                return new FloatNodeHandler();
            case STRING:
                return new StringNodeHandler();
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
} 