package com.home911.httpchat.server.exception;

import org.apache.http.HttpStatus;

public class ContactAlreadyExistException extends HttpChatException {
    public ContactAlreadyExistException() {
        super(HttpStatus.SC_CONFLICT, "Contact Already Exist.");
    }
}
