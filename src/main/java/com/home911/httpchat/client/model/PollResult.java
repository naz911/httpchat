package com.home911.httpchat.client.model;

import com.home911.httpchat.shared.model.Alert;
import com.home911.httpchat.shared.model.Message;

import java.util.List;

public class PollResult extends StatusResult {
    private List<Alert> alerts;
    private List<Message> messages;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PollResult{");
        sb.append(super.toString()).append(",")
                .append("alerts=").append(alerts)
                .append(", messages=").append(messages)
                .append('}');
        return sb.toString();
    }
}
