package com.satellite.protocol.core.check;

public class CrcCalculator {
    private static final int[] CRC16_TABLE = new int[256];
    private static final long[] CRC32_TABLE = new long[256];
    
    static {
        initCrc16Table();
        initCrc32Table();
    }
    
    public static int calculateCrc16(byte[] bytes) {
        int crc = 0xFFFF;
        for (byte b : bytes) {
            crc = (crc >>> 8) ^ CRC16_TABLE[(crc ^ b) & 0xFF];
        }
        return crc;
    }
    
    public static long calculateCrc32(byte[] bytes) {
        long crc = 0xFFFFFFFFL;
        for (byte b : bytes) {
            crc = (crc >>> 8) ^ CRC32_TABLE[(int) ((crc ^ b) & 0xFF)];
        }
        return crc ^ 0xFFFFFFFFL;
    }
    
    private static void initCrc16Table() {
        for (int i = 0; i < 256; i++) {
            int crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ 0xA001;
                } else {
                    crc = crc >>> 1;
                }
            }
            CRC16_TABLE[i] = crc;
        }
    }
    
    private static void initCrc32Table() {
        for (int i = 0; i < 256; i++) {
            long crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) == 1) {
                    crc = (crc >>> 1) ^ 0xEDB88320L;
                } else {
                    crc = crc >>> 1;
                }
            }
            CRC32_TABLE[i] = crc;
        }
    }
} 