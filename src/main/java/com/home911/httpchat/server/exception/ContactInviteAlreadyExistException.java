package com.home911.httpchat.server.exception;

import org.apache.http.HttpStatus;

public class ContactInviteAlreadyExistException extends HttpChatException {
    public ContactInviteAlreadyExistException() {
        super(HttpStatus.SC_CONFLICT, "Contact Invite Already Exist.");
    }
}
