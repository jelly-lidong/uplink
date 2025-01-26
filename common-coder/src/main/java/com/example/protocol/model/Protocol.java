package com.example.protocol.model;

import javax.xml.bind.annotation.*;

/**
 * 协议类，表示整个协议结构。
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "protocol")

public class Protocol {
    @XmlElement(name = "header")
    private Header header; // 单个 Header

    @XmlElement(name = "body")
    private Body body; // 单个 Body

    @XmlElement(name = "check")
    private Check check; // 单个 Check

    // Getter 和 Setter
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Check getCheck() {
        return check;
    }

    public void setCheck(Check check) {
        this.check = check;
    }
} 