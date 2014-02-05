package com.home911.httpchat.server.servlet.primitive;

public class LoginRequest {
    private String username;
    private String password;
    private boolean usePush = false;

    public LoginRequest(String username, String password, boolean usePush) {
        this.username = username;
        this.password = password;
        this.usePush = usePush;
    }

    public LoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUsePush() {
        return usePush;
    }

    public void setUsePush(boolean usePush) {
        this.usePush = usePush;
    }
}
