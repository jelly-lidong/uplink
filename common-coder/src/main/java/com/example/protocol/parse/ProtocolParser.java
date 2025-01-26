package com.example.protocol.parse;

import com.example.protocol.model.Protocol;

public interface ProtocolParser {
    Protocol toJavaObject(String xml);

    String parseObject(Protocol protocol);

}
