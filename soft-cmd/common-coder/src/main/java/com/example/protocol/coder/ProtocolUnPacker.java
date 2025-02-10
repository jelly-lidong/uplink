package com.example.protocol.coder;

import com.example.protocol.algorithm.Algorithm;
import com.example.protocol.algorithm.AlgorithmRegistry;
import com.example.protocol.enums.EndianType;
import com.example.protocol.model.*;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 协议解包器，负责将字节码解析为协议对象。
 */
public class ProtocolUnPacker {

    /**
     * 解包字节码为协议对象。
     *
     * @param protocol 协议对象，包含所有节点
     * @param byteData 字节码
     */
    public void unpack(Protocol protocol, byte[] byteData) {
        ByteBuffer buffer = ByteBuffer.wrap(byteData);

        // 解包主体
        unpackBody(protocol.getBody(), buffer);

        // 解包头部
        unpackHeader(protocol.getHeader(), buffer);

        // 解包检查
        unpackCheck(protocol.getCheck(), buffer);
    }

    private void unpackHeader(Header header, ByteBuffer buffer) {
        for (Node node : header.getNodes()) {
            unpackNode(node, buffer); // 解包当前头部的节点
        }
    }

    private void unpackBody(Body body, ByteBuffer buffer) {
        // 解包当前主体的节点
        List<Node> nodes = body.getNodes();
        if (!nodes.isEmpty()) {
            for (Node node : nodes) {
                unpackNode(node, buffer); // 解包当前主体的节点
            }
        }
        Check check = body.getCheck();
        if (check != null) {
            // 解包当前主体的检查
            unpackCheck(check, buffer); // 解包当前主体的检查
        }
        Body subBody = body.getBody();
        if (subBody != null) {
            // 如果有子主体，递归解包
            unpackBody(subBody, buffer); // 递归解包子主体
        }
        Header subHeader = body.getHeader();
        if (subHeader != null) {
            unpackHeader(subHeader, buffer); // 递归解包子头部
        }

    }

    private void unpackCheck(Check check, ByteBuffer buffer) {
        for (Node node : check.getNodes()) {
            unpackNode(node, buffer); // 解包当前检查的节点
        }
    }

    private void unpackNode(Node node, ByteBuffer buffer) {
        int length = node.getLength();
        boolean isLittleEndian = node.getEndianType() == EndianType.LITTLE_BYTE;

        // 根据节点的类型进行解包
        switch (node.getValueType()) {
            case UNSIGNED_INT:
                int unsignedIntValue = isLittleEndian ? Integer.reverseBytes(buffer.getInt()) : buffer.getInt();
                node.setValue(String.valueOf(unsignedIntValue)); // 设置节点值
                break;
            case SIGNED_INT:
                int signedIntValue = isLittleEndian ? Integer.reverseBytes(buffer.getInt()) : buffer.getInt();
                node.setValue(String.valueOf(signedIntValue)); // 设置节点值
                break;
            case FLOAT:
                float floatValue = isLittleEndian ? Float.intBitsToFloat(Integer.reverseBytes(buffer.getInt())) : buffer.getFloat();
                node.setValue(String.valueOf(floatValue)); // 设置节点值
                break;
            case BINARY:
                byte[] binaryData = new byte[length];
                buffer.get(binaryData); // 读取字节数据
                node.setValue(new String(binaryData)); // 设置节点值
                break;
            default:
                throw new IllegalArgumentException("unsupported value type: " + node.getValueType());
        }

        // 如果节点有逆向转换方法，调用它
        if (StringUtils.isNoneEmpty(node.getReverseCalFuncName())) {
            // 这里可以实现逆向转换逻辑
            // 例如：调用相应的算法进行转换
            Algorithm<Node> algorithm = (Algorithm<Node>) AlgorithmRegistry.getAlgorithm(node.getReverseCalFuncName());
            algorithm.execute(node); // 计算值

        }
    }
} 