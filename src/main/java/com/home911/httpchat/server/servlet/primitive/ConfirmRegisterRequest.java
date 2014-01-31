package com.home911.httpchat.server.servlet.primitive;

public class ConfirmRegisterRequest {
    private final String code;

    public ConfirmRegisterRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
