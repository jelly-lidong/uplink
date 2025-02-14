package com.satellite.protocol.core.expression;

import com.satellite.protocol.model.Node;
import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.model.Protocol;
import com.satellite.protocol.model.ProtocolBody;
import com.satellite.protocol.model.NodeGroup;

public class ReferenceExpression implements Expression {
    private final String reference;
    
    public ReferenceExpression(String expression) {
        // 解析${node1}.value格式的表达式
        this.reference = expression.substring(2, expression.length() - 1);
    }
    
    @Override
    public Object evaluate(Object value, ExpressionContext context) throws ProtocolException {
        try {
            String[] parts = reference.split("\\.");
            Node currentNode = context.getCurrentNode();
            Node targetNode = findNode(parts[0], currentNode);
            
            if (parts.length > 1) {
                switch (parts[1]) {
                    case "value":
                        return targetNode.getValue();
                    case "length":
//                        if (targetNode instanceof NodeGroup) {
//                            return calculateNodeGroupLength((NodeGroup)targetNode);
//                        }
                        return targetNode.getByteLength();
                    default:
                        throw new ProtocolException("Unknown reference property: " + parts[1]);
                }
            }
            return targetNode.getValue();
        } catch (Exception e) {
            throw new ProtocolException("Failed to evaluate reference: " + reference, e);
        }
    }
    
    private Node findNode(String path, Node currentNode) throws ProtocolException {
        if (path == null || path.isEmpty() || currentNode == null) {
            throw new ProtocolException("Invalid node path: " + path);
        }

        String[] pathParts = path.split("/");
        Node searchNode = currentNode;
        int startIndex = 0;

        // 处理相对路径
        while (startIndex < pathParts.length) {
            String part = pathParts[startIndex];
            if ("..".equals(part)) {
                // 向上一级
                if (searchNode.getHeader() != null) {
                    searchNode = searchNode.getHeader().getNodes().get(0);
                } else if (searchNode.getBody() != null) {
                    searchNode = searchNode.getBody().getHeader().getNodes().get(0);
                } else if (searchNode.getGroup() != null) {
                    searchNode = searchNode.getGroup().getBody().getNodes().get(0);
                }
            } else if (".".equals(part)) {
                // 当前级别
                // 不需要改变searchNode
            } else {
                // 查找目标节点
                if (searchNode.getProtocol() != null) {
                    Protocol protocol = searchNode.getProtocol();
                    // 在header中查找
                    if (protocol.getHeader() != null) {
                        for (Node node : protocol.getHeader().getNodes()) {
                            if (part.equals(node.getName())) {
                                return node;
                            }
                        }
                    }
                    // 在body中查找
                    if (protocol.getBody() != null) {
                        // 查找节点
                        if (protocol.getBody().getNodes() != null) {
                            for (Node node : protocol.getBody().getNodes()) {
                                if (part.equals(node.getName())) {
                                    return node;
                                }
                            }
                        }
                        // 查找节点组
                        if (protocol.getBody().getNodeGroups() != null) {
                            for (NodeGroup group : protocol.getBody().getNodeGroups()) {
                                if (part.equals(group.getName())) {
                                    return group.getNodes().get(0); // 返回组的第一个节点
                                }
                            }
                        }
                    }
                }
            }
            startIndex++;
        }
        throw new ProtocolException("Node not found: " + path);
    }

    private int calculateNodeGroupLength(NodeGroup group) {
        int totalLength = 0;
        if (group.getNodes() != null) {
            for (Node node : group.getNodes()) {
                totalLength += node.getByteLength();
            }
        }
        // 如果有repeat属性，需要乘以重复次数
        try {
            int repeat = Integer.parseInt(group.getRepeat());
            return totalLength * repeat;
        } catch (NumberFormatException e) {
            // 如果repeat不是数字，可能是表达式，暂时返回单次长度
            return totalLength;
        }
    }
} 