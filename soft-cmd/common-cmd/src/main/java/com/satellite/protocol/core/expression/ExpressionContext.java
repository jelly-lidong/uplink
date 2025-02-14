package com.satellite.protocol.core.expression;

import com.satellite.protocol.model.Node;
import java.util.Map;
import java.util.HashMap;

public class ExpressionContext {
    private final Node currentNode;
    private final Map<String, Object> variables;
    
    public ExpressionContext(Node currentNode) {
        this.currentNode = currentNode;
        this.variables = new HashMap<>();
    }
    
    public Node getCurrentNode() {
        return currentNode;
    }
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public Object getVariable(String name) {
        return variables.get(name);
    }
} 