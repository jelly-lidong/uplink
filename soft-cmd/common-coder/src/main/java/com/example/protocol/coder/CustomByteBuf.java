package com.example.protocol.coder;

import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;

/**
 * 自定义 ByteBuf 包装类，扩展 Netty 的 UnpooledHeapByteBuf，支持按位写入和读取。
 */
public class CustomByteBuf extends UnpooledHeapByteBuf {
    private int writeBitOffset; // 当前写入位偏移量
    private int readBitOffset;  // 当前读取位偏移量

    public CustomByteBuf(int initialCapacity) {
        super(new UnpooledByteBufAllocator(true), initialCapacity, Integer.MAX_VALUE);
        this.writeBitOffset = 0; // 初始化写入位偏移量
        this.readBitOffset = 0;  // 初始化读取位偏移量
    }

    /**
     * 写入多个比特值。
     *
     * @param values 比特值数组（0 或 1）
     */
    public synchronized void writeBits(int[] values) {
        for (int value : values) {
            if (value != 0 && value != 1) {
                throw new IllegalArgumentException("Value must be 0 or 1");
            }
            writeBit(value);
        }
    }

    /**
     * 写入一个比特值。
     *
     * @param value 比特值（0 或 1）
     */
    private synchronized void writeBit(int value) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException("Value must be 0 or 1");
        }

        // 计算字节和位
        int byteIndex = writeBitOffset / 8;
        int bitIndex = writeBitOffset % 8;

        // 确保 ByteBuf 有足够的空间
        if (byteIndex >= capacity()) {
            ensureWritable(byteIndex + 1);
        }

        // 写入比特
        setByte(byteIndex, (byte) (getByte(byteIndex) | (value << bitIndex)));
        writeBitOffset++;
    }

    /**
     * 读取多个比特值。
     *
     * @param count 要读取的比特数量
     * @return 读取的比特值数组
     */
    public synchronized int[] readBits(int count) {
        int[] values = new int[count];
        for (int i = 0; i < count; i++) {
            values[i] = readBit();
        }
        return values;
    }

    /**
     * 读取一个比特值。
     *
     * @return 读取的比特值（0 或 1）
     */
    private synchronized int readBit() {
        int byteIndex = readBitOffset / 8;
        int bitIndex = readBitOffset % 8;

        // 确保在可读范围内
        if (byteIndex >= readableBytes()) {
            throw new IndexOutOfBoundsException("No more bits to read");
        }

        // 读取比特
        int value = (getByte(byteIndex) >> bitIndex) & 0x01;
        readBitOffset++;
        return value;
    }

    /**
     * 写入指定字节长度的整数。
     *
     * @param value 要写入的整数值
     * @param byteLength 要占用的字节长度
     */
    public synchronized void writeLengthInt(int value, int byteLength) {
        for (int i = 0; i < byteLength; i++) {
            writeBit((value >> (i * 8)) & 0xFF); // 按字节写入
        }
    }

    /**
     * 读取指定字节长度的整数。
     *
     * @param byteLength 要读取的字节长度
     * @return 读取的整数值
     */
    public synchronized int readLengthInt(int byteLength) {
        int value = 0;
        for (int i = 0; i < byteLength; i++) {
            value |= (readBit() & 0xFF) << (i * 8); // 按字节读取
        }
        return value;
    }

    /**
     * 按字节序写入整数。
     *
     * @param value 要写入的整数值
     * @param byteLength 要占用的字节长度
     * @param isLittleEndian 是否为小端
     */
    public synchronized void writeIntWithEndian(int value, int byteLength, boolean isLittleEndian) {
        for (int i = 0; i < byteLength; i++) {
            int byteValue = isLittleEndian ? (value >> (i * 8)) & 0xFF : (value >> ((byteLength - 1 - i) * 8)) & 0xFF;
            writeBit(byteValue);
        }
    }

} 