package com.home911.httpchat.server.servlet.primitive;

import com.home911.httpchat.shared.model.Contact;

import java.util.List;

public class GetContactsResponse extends StatusResponse {
    private List<Contact> contacts;

    public GetContactsResponse(int code, String description, List<Contact> contacts) {
        super(code, description);
        this.contacts = contacts;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
