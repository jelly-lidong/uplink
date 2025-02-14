package com.satellite.protocol.core;

import com.satellite.protocol.core.composite.ProtocolComponent;
import com.satellite.protocol.core.composite.ProtocolComposite;
import com.satellite.protocol.core.context.ProtocolContext;
import com.satellite.protocol.model.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CmdProtocolCodec implements ProtocolCodec {

  @Override
  public byte[] encode(Protocol protocol) throws ProtocolException {
    ByteBuf buffer = Unpooled.buffer();
    try {
      ProtocolComponent root = new ProtocolComposite(protocol);
      ProtocolContext context = new ProtocolContext(root);
      root.encode(buffer, context);

      byte[] result = new byte[buffer.readableBytes()];
      buffer.readBytes(result);
      return result;
    } finally {
      buffer.release();
    }
  }

  @Override
  public Protocol decode(byte[] bytes) throws ProtocolException {
    ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
    try {
      Protocol          protocol = new Protocol();
      ProtocolComponent root     = new ProtocolComposite(protocol);
      ProtocolContext context = new ProtocolContext(root);
      root.decode(buffer, context);
      return protocol;
    } finally {
      buffer.release();
    }
  }
} 