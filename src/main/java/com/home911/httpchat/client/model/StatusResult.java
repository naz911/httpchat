package com.home911.httpchat.client.model;

import com.home911.httpchat.shared.model.Status;

public class StatusResult {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatusResult{");
        sb.append("status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
