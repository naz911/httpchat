package com.home911.httpchat.exception;

public abstract class HttpChatException extends RuntimeException {
    private final int status;
    private final String description;

    protected HttpChatException(int status, String description) {
        super();
        this.status = status;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
