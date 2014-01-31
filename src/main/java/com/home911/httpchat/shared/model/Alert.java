package com.home911.httpchat.shared.model;

import java.io.Serializable;

public class Alert implements Serializable {
    private Long id;
    private NotificationType type;
    private Long referer;
    private Object data;

    public Alert() {
    }

    public Alert(Long id, NotificationType type, Long referer, Object data) {
        this.id = id;
        this.type = type;
        this.referer = referer;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Long getReferer() {
        return referer;
    }

    public void setReferer(Long referer) {
        this.referer = referer;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Alert{");
        sb.append("id=").append(id);
        sb.append(", type=").append(type);
        sb.append(", referer=").append(referer);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
