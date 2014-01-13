package com.home911.httpchat.servlet.model;

import com.home911.httpchat.model.NotificationType;

public class Alert {
    private final Long id;
    private final NotificationType type;
    private final Long referer;
    private final Object data;

    public Alert(Long id, NotificationType type, Long referer, Object data) {
        this.id = id;
        this.type = type;
        this.referer = referer;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public Long getReferer() {
        return referer;
    }

    public Object getData() {
        return data;
    }
}
