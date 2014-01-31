package com.home911.httpchat.server.exception;

import org.apache.http.HttpStatus;

public class UnknownUserException extends HttpChatException {
    public UnknownUserException() {
        super(HttpStatus.SC_NOT_FOUND, "Unknown user.");
    }
}
