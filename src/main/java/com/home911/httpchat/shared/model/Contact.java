package com.home911.httpchat.shared.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private Long id;
    private String name;
    private Presence presence;

    public Contact() {
    }

    public Contact(Long id, String name, Presence presence) {
        this.id = id;
        this.name = name;
        this.presence = presence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Contact{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", presence=").append(presence);
        sb.append('}');
        return sb.toString();
    }
}
