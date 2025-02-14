package com.satellite.protocol.model.enums;

public enum CheckType {
    CRC16("CRC16"),
    CRC32("CRC32"),
    CHECKSUM("CHECKSUM"),
    XOR("XOR");
    
    private final String value;
    
    CheckType(String value) {
        this.value = value;
    }
} 