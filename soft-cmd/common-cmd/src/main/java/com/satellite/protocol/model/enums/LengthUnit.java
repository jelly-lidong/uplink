package com.satellite.protocol.model.enums;

import lombok.Getter;

@Getter
public enum LengthUnit {
    BYTE("BYTE"),
    BIT("BIT");
    
    private final String value;
    
    LengthUnit(String value) {
        this.value = value;
    }

  public static LengthUnit fromValue(String value) {
        for (LengthUnit unit : LengthUnit.values()) {
            if (unit.value.equals(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown LengthUnit: " + value);
    }
} 