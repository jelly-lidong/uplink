package com.satellite.protocol.model.enums;

public enum ByteOrder {
    BIG("BIG"),
    LITTLE("LITTLE");
    
    private final String value;
    
    ByteOrder(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}