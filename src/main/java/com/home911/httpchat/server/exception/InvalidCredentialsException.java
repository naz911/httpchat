package com.home911.httpchat.server.exception;

import org.apache.http.HttpStatus;

public class InvalidCredentialsException extends HttpChatException {
    public InvalidCredentialsException() {
        super(HttpStatus.SC_UNAUTHORIZED, "Invalid Credentials.");
    }
}
