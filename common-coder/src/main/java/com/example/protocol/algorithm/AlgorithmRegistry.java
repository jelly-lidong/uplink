package com.example.protocol.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 算法注册表，负责加载和管理所有算法插件。
 */
public class AlgorithmRegistry {
    
    private static final Map<String, Algorithm<?>> algorithmMap = new HashMap<>();
    private static boolean isLoaded = false; // 标志，指示算法是否已加载

    /**
     * 加载所有算法插件。
     *
     * @return 算法插件列表
     */
    public static synchronized List<Algorithm<?>> loadAlgorithms() {
        if (isLoaded) {
            return new ArrayList<>(algorithmMap.values()); // 如果已加载，直接返回已加载的算法
        }

        List<Algorithm<?>> algorithms = new ArrayList<>();
        ServiceLoader<Algorithm> loader = ServiceLoader.load(Algorithm.class);
        for (Algorithm<?> algorithm : loader) {
            algorithms.add(algorithm);
            // 将算法类型和名称映射到算法实例
            String key =  algorithm.getName();
            algorithmMap.put(key, algorithm);
        }
        isLoaded = true; // 设置为已加载
        return algorithms;
    }

    /**
     * 根据算法类型和名称获取算法实例。
     *
     * @param name 算法名称
     * @return 算法实例
     */
    public static Algorithm<?> getAlgorithm(String name) {
        return algorithmMap.get(name);
    }


} 