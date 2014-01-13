package com.home911.httpchat.servlet.event;

public class ResponseEvent<T> {
    private final T response;

    public ResponseEvent(T response) {
        this.response = response;
    }

    public T getResponse() {
        return response;
    }
}
