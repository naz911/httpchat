package com.home911.httpchat.servlet.model;

public class Message {
    private Long to;
    private Long from;
    private String text;

    public Message() {
    }

    public Message(Long from, String text) {
        this.to = from;
        this.text = text;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
