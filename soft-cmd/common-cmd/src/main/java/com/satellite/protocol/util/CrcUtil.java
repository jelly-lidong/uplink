package com.satellite.protocol.util;

public class CrcUtil {
    // CRC-16/MODBUS 多项式：x16 + x15 + x2 + 1 (0x8005)
    private static final int CRC16_POLYNOMIAL = 0x8005;
    
    // CRC-32 多项式：x32 + x26 + x23 + x22 + x16 + x12 + x11 + x10 + x8 + x7 + x5 + x4 + x2 + x + 1 (0x04C11DB7)
    private static final int CRC32_POLYNOMIAL = 0x04C11DB7;
    
    public static byte[] calculateCRC16(byte[] data) {
        int crc = 0xFFFF; // 初始值
        
        for (byte b : data) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ CRC16_POLYNOMIAL;
                } else {
                    crc = crc << 1;
                }
            }
        }
        
        return new byte[] {
            (byte) ((crc >> 8) & 0xFF),
            (byte) (crc & 0xFF)
        };
    }
    
    public static byte[] calculateCRC32(byte[] data) {
        int crc = 0xFFFFFFFF; // 初始值
        
        for (byte b : data) {
            crc ^= (b & 0xFF) << 24;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80000000) != 0) {
                    crc = (crc << 1) ^ CRC32_POLYNOMIAL;
                } else {
                    crc = crc << 1;
                }
            }
        }
        
        crc = ~crc; // 最终结果取反
        return new byte[] {
            (byte) ((crc >> 24) & 0xFF),
            (byte) ((crc >> 16) & 0xFF),
            (byte) ((crc >> 8) & 0xFF),
            (byte) (crc & 0xFF)
        };
    }
}