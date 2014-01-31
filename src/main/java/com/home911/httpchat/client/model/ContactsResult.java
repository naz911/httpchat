package com.home911.httpchat.client.model;

import com.home911.httpchat.shared.model.Contact;

import java.util.List;

public class ContactsResult extends StatusResult {
    private List<Contact> contacts;

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ContactsResult{");
        sb.append(super.toString()).append(",")
                .append("contacts=").append(contacts)
                .append('}');
        return sb.toString();
    }
}
