package com.satellite.protocol.core;

import com.satellite.protocol.model.Protocol;

public interface ProtocolCodec {
    byte[] encode(Protocol protocol) throws ProtocolException;
    Protocol decode(byte[] bytes) throws ProtocolException;
} 