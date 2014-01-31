package com.home911.httpchat.server.exception;

import org.apache.http.HttpStatus;

public class UserNotActivatedException extends HttpChatException {
    public UserNotActivatedException() {
        super(HttpStatus.SC_PRECONDITION_FAILED, "User not activated.");
    }
}
