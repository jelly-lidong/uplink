package com.example.protocol.algorithm.impl;

import com.example.protocol.model.Node;
import com.example.protocol.algorithm.Algorithm;

/**
 * 计算body的长度。
 */
public class BodyCrcAlgorithm implements Algorithm<Node> {

    @Override
    public void execute(Node node) {

    }

    @Override
    public String getName() {
        return "CRC计算";
    }

    @Override
    public String getType() {
        return "校验";
    }
} 