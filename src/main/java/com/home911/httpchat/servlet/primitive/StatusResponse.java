package com.home911.httpchat.servlet.primitive;

import com.home911.httpchat.servlet.model.Alert;
import com.home911.httpchat.servlet.model.Status;

import java.util.List;

public class StatusResponse {
    private final Status status;
    private List<Alert> alerts;

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
}