package com.home911.httpchat.shared.model;

import java.io.Serializable;

public class Message implements Serializable {
    private Long to;
    private Contact from;
    private String text;

    public Message() {
    }

    public Message(Contact from, String text) {
        this.from = from;
        this.text = text;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public Contact getFrom() {
        return from;
    }

    public void setFrom(Contact from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
