package com.home911.httpchat.server.exception;

import org.apache.http.HttpStatus;

public class ServerErrorException extends HttpChatException {
    public ServerErrorException() {
        super(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Unexpected Server Error.");
    }
}
