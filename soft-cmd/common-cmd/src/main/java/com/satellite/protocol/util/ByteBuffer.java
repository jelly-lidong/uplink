package com.satellite.protocol.util;

import java.util.Arrays;

public class ByteBuffer {
    private byte[] buffer;
    private int position;
    
    public ByteBuffer() {
        this.buffer = new byte[1024];
        this.position = 0;
    }
    
    public ByteBuffer(byte[] bytes) {
        this.buffer = bytes;
        this.position = 0;
    }
    
    public void write(byte[] bytes) {
        ensureCapacity(position + bytes.length);
        System.arraycopy(bytes, 0, buffer, position, bytes.length);
        position += bytes.length;
    }
    
    public byte[] read(int length) {
        byte[] result = new byte[length];
        System.arraycopy(buffer, position, result, 0, length);
        position += length;
        return result;
    }
    
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > buffer.length) {
            int newCapacity = Math.max(buffer.length << 1, minCapacity);
            buffer = Arrays.copyOf(buffer, newCapacity);
        }
    }
    
    public byte[] toBytes() {
        return Arrays.copyOf(buffer, position);
    }
} 