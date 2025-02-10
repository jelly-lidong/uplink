package com.satellite.protocol.model;

import com.satellite.protocol.model.adapter.LengthUnitAdapter;
import com.satellite.protocol.model.enums.LengthUnit;
import javax.xml.bind.annotation.*;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ProtocolCheck {
    @XmlAttribute
    private String name;
    
    @XmlAttribute
    private int length;

    @XmlAttribute
    @XmlJavaTypeAdapter(LengthUnitAdapter.class)
    private LengthUnit lengthUnit;
    
    @XmlElement(name = "node")
    private List<Node> nodes;

} 