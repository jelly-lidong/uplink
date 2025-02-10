package com.satellite.protocol.model.adapter;

import com.satellite.protocol.model.enums.NodeType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class NodeTypeAdapter extends XmlAdapter<String, NodeType> {
    @Override
    public NodeType unmarshal(String value) {
        return NodeType.fromValue(value);
    }
    
    @Override
    public String marshal(NodeType type) {
        return type != null ? type.getValue() : null;
    }
} 