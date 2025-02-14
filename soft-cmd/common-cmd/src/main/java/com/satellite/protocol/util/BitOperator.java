package com.satellite.protocol.util;

public class BitOperator {
    
    public static byte[] getBits(byte b, int offset, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) ((b >> (7 - offset - i)) & 0x01);
        }
        return result;
    }
    
    public static byte setBits(byte original, int offset, int length, byte[] bits) {
        byte result = original;
        for (int i = 0; i < length; i++) {
            result = (byte) (result & ~(1 << (7 - offset - i)));
            result = (byte) (result | ((bits[i] & 0x01) << (7 - offset - i)));
        }
        return result;
    }
} 