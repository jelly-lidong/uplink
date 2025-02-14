package com.satellite.protocol.core.function;

import com.satellite.protocol.core.ProtocolException;
import com.satellite.protocol.core.check.CrcCalculator;
import com.satellite.protocol.model.Node;
import com.satellite.protocol.model.ProtocolBody;

public class Crc16Function extends AbstractCheckFunction {
    @Override
    public Object execute(Object... args) throws ProtocolException {
        if (args.length != 1) {
            throw new ProtocolException("CRC16 function requires 1 argument");
        }
        
        if (args[0] instanceof Node) {
            Node node = (Node) args[0];
            byte[] data = getNodeBytes(node);
            return CrcCalculator.calculateCrc16(data);
        } else if (args[0] instanceof ProtocolBody) {
            ProtocolBody body = (ProtocolBody) args[0];
            byte[] data = getBodyBytes(body);
            return CrcCalculator.calculateCrc16(data);
        }
        
        throw new ProtocolException("Invalid argument type for CRC16: " + args[0].getClass());
    }
    
    @Override
    public String getName() {
        return "crc16";
    }
} 