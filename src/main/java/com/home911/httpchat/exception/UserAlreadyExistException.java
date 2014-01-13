package com.home911.httpchat.exception;

import org.apache.http.HttpStatus;

public class UserAlreadyExistException extends HttpChatException {
    public UserAlreadyExistException() {
        super(HttpStatus.SC_CONFLICT, "User Already Exist.");
    }
}
