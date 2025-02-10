package com.satellite.protocol.model.adapter;

import com.satellite.protocol.model.enums.LengthUnit;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LengthUnitAdapter extends XmlAdapter<String, LengthUnit> {
    @Override
    public LengthUnit unmarshal(String value) {
        return LengthUnit.fromValue(value);
    }
    
    @Override
    public String marshal(LengthUnit unit) {
        return unit != null ? unit.getValue() : null;
    }
} 