package com.home911.httpchat.shared.model;

import java.io.Serializable;

public class Push implements Serializable {
    private Alert alert;
    private Message message;

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Push{");
        sb.append("alert=").append(alert);
        sb.append(", message=").append(message);
        sb.append('}');
        return sb.toString();
    }
}
