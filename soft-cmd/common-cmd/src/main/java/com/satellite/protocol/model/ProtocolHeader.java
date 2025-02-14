package com.satellite.protocol.model;

import javax.xml.bind.annotation.*;
import java.util.List;
import com.satellite.protocol.model.enums.LengthUnit;
import com.satellite.protocol.model.adapter.LengthUnitAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ProtocolHeader {
    @XmlAttribute
    private String name;
    
    @XmlAttribute
    private int length;
    
    @XmlAttribute
    @XmlJavaTypeAdapter(LengthUnitAdapter.class)
    private LengthUnit lengthUnit;
    
    @XmlElement(name = "node")
    private List<Node> nodes;

    @XmlTransient
    private Protocol protocol;
    
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
        if (nodes != null) {
            nodes.forEach(node -> node.setHeader(this));
        }
    }
} 