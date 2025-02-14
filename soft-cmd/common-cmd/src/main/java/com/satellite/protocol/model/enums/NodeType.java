package com.satellite.protocol.model.enums;

public enum NodeType {
    INT("INT"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    HEX("HEX"),
    BIT("BIT"),
    ENUM("ENUM"),
    TIMESTAMP("TIMESTAMP"),
    DYNAMIC("DYNAMIC"),
    PADDING("PADDING"),
    CRC16("CRC16"),
    CRC32("CRC32"),
    STRING("STRING");
    
    private final String value;
    
    NodeType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static NodeType fromValue(String value) {
        for (NodeType type : NodeType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown NodeType: " + value);
    }
} 