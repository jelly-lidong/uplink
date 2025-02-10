package com.satellite.protocol.util;

import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.Protocol;
import com.satellite.protocol.model.ProtocolBody;
import com.satellite.protocol.model.ProtocolHeader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class CheckRangeParser {
    // 用于缓存已处理的字节数据
    private static final Map<String, byte[]> processedBytesCache = new ConcurrentHashMap<>();
    
    /**
     * 解析checkRange表达式并获取对应的字节数据
     * checkRange表达式指定了需要校验的节点范围
     * 从protocol中查找对应的节点并获取其字节数据
     */
    public static byte[] parseCheckRange(String checkRange, Protocol protocol) {
        // 检查参数
        if (checkRange == null || protocol == null) {
            throw new IllegalArgumentException("checkRange和protocol不能为空");
        }

        // 检查缓存
        byte[] cachedBytes = processedBytesCache.get(checkRange);
        if (cachedBytes != null) {
            return cachedBytes;
        }

        List<byte[]> dataToCheck = new ArrayList<>();
        Queue<String> nodesToProcess = new LinkedList<>();

        // 将checkRange分解为节点
        String[] nodes = checkRange.split("/");
        for (String node : nodes) {
            nodesToProcess.add(node);
        }

        // 处理节点
        ProtocolBody currentBody = protocol.getBody(); // 当前上下文的协议体
        ProtocolHeader currentHeader = protocol.getHeader(); // 当前上下文的协议头

        while (!nodesToProcess.isEmpty()) {
            String currentNode = nodesToProcess.poll();

            if (currentNode.equals(".")) {
                // 当前层级，继续处理下一个节点
                continue;
            } else if (currentNode.equals("..")) {
                // 上一层级，更新当前上下文
                if (currentBody != null) {
                    currentBody = currentBody.getSubBody(); // 向上查找
                    currentHeader = currentBody != null ? currentBody.getHeader() : null;
                }
                continue;
            }

            // 查找当前上下文中的节点
            if (currentHeader != null) {
                findNodeInHeader(currentHeader, currentNode, dataToCheck);
            }
            if (currentBody != null) {
                findNodeInBody(currentBody, currentNode, dataToCheck);
            }
        }

        if (dataToCheck.isEmpty()) {
            throw new IllegalArgumentException("找不到指定的节点: " + checkRange);
        }

        byte[] result = mergeBytes(dataToCheck);
        // 存入缓存
        processedBytesCache.put(checkRange, result);
        return result;
    }

    private static void findNodeInHeader(ProtocolHeader header, String nodeName, List<byte[]> collector) {
        if (header.getNodes() != null) {
            for (Node node : header.getNodes()) {
                if (node.getName().equals(nodeName)) {
                    collector.add(ProtocolBinaryConverter.processNode(node));
                    return;
                }
            }
        }
    }

    private static void findNodeInBody(ProtocolBody body, String nodeName, List<byte[]> collector) {
        if (body.getHeader() != null) {
            findNodeInHeader(body.getHeader(), nodeName, collector);
        }
        if (body.getSubBody() != null) {
            findNodeInBody(body.getSubBody(), nodeName, collector);
        }
    }

    private static byte[] mergeBytes(List<byte[]> byteArrays) {
        int totalLength = byteArrays.stream().mapToInt(arr -> arr.length).sum();
        byte[] result = new byte[totalLength];
        int offset = 0;

        for (byte[] array : byteArrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        processedBytesCache.clear();
    }
} 