package com.home911.httpchat.server.servlet.primitive;

public class RegisterResponse extends StatusResponse {
    private final String confirmUrl;

    public RegisterResponse(int code, String description, String confirmUrl) {
        super(code, description);
        this.confirmUrl = confirmUrl;
    }

    public String getConfirmUrl() {
        return confirmUrl;
    }
}
