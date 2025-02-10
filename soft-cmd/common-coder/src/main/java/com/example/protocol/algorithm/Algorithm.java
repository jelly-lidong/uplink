package com.example.protocol.algorithm;

/**
 * 算法接口，定义算法的基本行为。
 */
public interface Algorithm<T> {
    void execute(T input);

    String getName();

    String getType();
} 