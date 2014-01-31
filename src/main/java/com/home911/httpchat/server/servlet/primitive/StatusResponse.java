package com.home911.httpchat.server.servlet.primitive;

import com.home911.httpchat.shared.model.Alert;
import com.home911.httpchat.shared.model.Message;
import com.home911.httpchat.shared.model.Status;

import java.util.List;

public class StatusResponse {
    private final Status status;
    private List<Alert> alerts;
    private List<Message> messages;

    public StatusResponse(int code, String description) {
        this.status = new Status(code, description);
    }

    public Status getStatus() {
        return status;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
