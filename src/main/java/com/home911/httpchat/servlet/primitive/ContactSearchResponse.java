package com.home911.httpchat.servlet.primitive;

import com.home911.httpchat.servlet.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactSearchResponse extends StatusResponse {

    private List<Contact> contacts;

    public ContactSearchResponse(int code, String description, List<Contact> contacts) {
        super(code, description);
        this.contacts = contacts;
    }

    public ContactSearchResponse(int code, String description) {
        super(code, description);
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void addContact(Contact contact) {
        if (contacts == null) {
            contacts = new ArrayList<Contact>();
        }
        contacts.add(contact);
    }
}
