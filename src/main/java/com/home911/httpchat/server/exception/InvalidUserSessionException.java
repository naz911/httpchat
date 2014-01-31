package com.home911.httpchat.server.exception;

import org.apache.http.HttpStatus;

public class InvalidUserSessionException extends HttpChatException {
    public InvalidUserSessionException() {
        super(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Current User Does Not Exist.");
    }
}
