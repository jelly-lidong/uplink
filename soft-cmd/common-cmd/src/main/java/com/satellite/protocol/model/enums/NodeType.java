package com.satellite.protocol.model.enums;

public enum NodeType {
    HEX("HEX"),
    BIT("BIT"),
    INT("INT"),
    ENUM("ENUM"),
    TIMESTAMP("TIMESTAMP"),
    DYNAMIC("DYNAMIC"),
    PADDING("PADDING"),
    CRC16("CRC16"),
    CRC32("CRC32");
    
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