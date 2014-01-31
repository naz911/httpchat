package com.home911.httpchat.server.exception;

import org.apache.http.HttpStatus;

public class UserOfflineException extends HttpChatException {
    public UserOfflineException() {
        super(HttpStatus.SC_GONE, "User Offline.");
    }
}
