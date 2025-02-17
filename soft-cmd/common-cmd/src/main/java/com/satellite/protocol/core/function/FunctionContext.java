package com.satellite.protocol.core.function;

import com.satellite.protocol.core.context.ProtocolContext;
import java.util.Map;
import java.util.HashMap;
import lombok.Getter;

public class FunctionContext {
    @Getter
    private final ProtocolContext protocolContext;
    private final Map<String, Object> variables;
    
    public FunctionContext(ProtocolContext protocolContext) {
        this.protocolContext = protocolContext;
        this.variables = new HashMap<>();
    }
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public Object getVariable(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        return protocolContext.getNodeValue(name);
    }
} 