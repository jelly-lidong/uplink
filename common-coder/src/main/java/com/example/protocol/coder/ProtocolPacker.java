package com.example.protocol.coder;

import com.example.protocol.algorithm.Algorithm;
import com.example.protocol.algorithm.AlgorithmRegistry;
import com.example.protocol.enums.EndianType;
import com.example.protocol.enums.LengthUnit;
import com.example.protocol.enums.ValueType;
import com.example.protocol.model.*;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 协议打包器，负责将协议打包成指令码字。
 */
public class ProtocolPacker {

    /**
     * 打包协议为指令码字。
     *
     * @param protocol 协议对象
     * @return 打包后的字节数组
     */
    public byte[] pack(Protocol protocol) {
        CustomByteBuf customByteBuf = new CustomByteBuf(1024); // 预分配足够的空间
        Body body = protocol.getBody();
        packBody(body, customByteBuf); // 从内到外打包

        Header header = protocol.getHeader();
        packHeader(header, body, customByteBuf); // 打包头部

        Check check = protocol.getCheck();
        packCheck(check, body, customByteBuf); // 打包检查

        byte[] packedData = new byte[customByteBuf.readableBytes()];
        customByteBuf.readBytes(packedData); // 读取打包后的数据
        customByteBuf.release(); // 释放 ByteBuf
        return packedData;
    }

    private void packHeader(Header header, Body body, CustomByteBuf customByteBuf) {
        for (Node node : header.getNodes()) {
            packNode(node, body, customByteBuf); // 只传递当前头部的节点
        }
    }

    private void packBody(Body body, CustomByteBuf customByteBuf) {
        CustomByteBuf tmpBuf = new CustomByteBuf(1024); // 临时缓冲区
        // 先打包子主体
        Body subBody = body.getBody();
        if (subBody != null) {
            packBody(subBody, tmpBuf); // 递归打包子主体
        }
        Header subHeader = body.getHeader();
        if (subHeader != null) {
            packHeader(subHeader, subBody, tmpBuf); // 递归打包子头部
        }
        List<Node> subNodes = body.getNodes();
        if (subNodes != null) {
            // 打包当前主体的节点
            for (Node node : subNodes) {
                packNode(node, null, tmpBuf); // 只传递当前主体的节点
            }
        }
        Check check = body.getCheck();
        if (check != null) {
            // 打包当前主体的检查
            packCheck(check, subBody, tmpBuf);
        }
        byte[] tmpData = new byte[tmpBuf.readableBytes()];
        tmpBuf.readBytes(tmpData); // 读取临时缓冲区中的有效字节
        body.setValue(HexBin.encode(tmpData));
        body.setValueType(ValueType.HEX);
        body.setLength(tmpData.length);
        body.setLengthUnit(LengthUnit.BYTE);
        customByteBuf.writeBytes(tmpData);
    }

    private void packCheck(Check check, Body body, CustomByteBuf customByteBuf) {
        for (Node node : check.getNodes()) {
            packNode(node, body, customByteBuf); // 只传递当前检查的节点
        }
    }

    private void packNode(Node node, Body body, CustomByteBuf customByteBuf) {
        // 节点的值需要转换
        String calFuncName = node.getCalFuncName();
        if (StringUtils.isNoneEmpty(calFuncName)) {
            calculateValue(node); // 只依赖于同级节点
        }

        //节点依赖于body
//        if (node.getDepOnBody()) {
//            node.setOnBody(body);
//            calculateValue(node); // 只依赖于同级节点
//        }

        String value = node.getValue();
        ValueType valueType = node.getValueType();
        int length = node.getLength();
        boolean isLittleEndian = node.getEndianType() == EndianType.LITTLE_BYTE;

        // 根据节点的类型进行打包
        switch (valueType) {
            case UNSIGNED_INT:
                customByteBuf.writeIntWithEndian(Integer.parseUnsignedInt(value), length, isLittleEndian); // 写入无符号整数
                break;
            case SIGNED_INT:
                customByteBuf.writeIntWithEndian(Integer.parseInt(value), length, isLittleEndian); // 写入有符号整数
                break;
            case FLOAT:
                customByteBuf.writeFloat(Float.parseFloat(value)); // 写入浮点数
                break;
            case BINARY:
                int[] bitValues = new int[value.length()];
                for (int i = 0; i < value.length(); i++) {
                    bitValues[i] = Character.getNumericValue(value.charAt(i)); // 将字符转换为比特值
                }
                customByteBuf.writeBits(bitValues); // 写入比特值
                break;
            default:
                throw new IllegalArgumentException("unsupported value type: " + valueType);
        }
    }

    private void calculateValue(Node node) {
        String calFuncName = node.getCalFuncName();
        Algorithm<Node> algorithm = (Algorithm<Node>) AlgorithmRegistry.getAlgorithm(calFuncName);
        algorithm.execute(node); // 计算值
    }
} 