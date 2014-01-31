package com.home911.httpchat.server.servlet.event;

public class RequestEvent<T> {
    private final Long userId;
    private final T request;

    public RequestEvent(Long userId, T request) {
        this.userId = userId;
        this.request = request;
    }

    public RequestEvent(T request) {
        this(null, request);
    }

    public T getRequest() {
        return request;
    }

    public Long getUserId() {
        return userId;
    }
}
