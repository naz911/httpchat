package com.home911.httpchat.shared.model;

import java.io.Serializable;

public class Status implements Serializable {
    private int code;
    private String description;

    public Status() {
    }

    public Status(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Status{");
        sb.append("code=").append(code);
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
