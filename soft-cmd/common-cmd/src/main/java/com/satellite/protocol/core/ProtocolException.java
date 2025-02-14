package com.satellite.protocol.core;

public class ProtocolException extends Exception {
    
    public ProtocolException(String message) {
        super(message);
    }
    
    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
} 