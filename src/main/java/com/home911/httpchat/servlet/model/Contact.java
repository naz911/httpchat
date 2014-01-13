package com.home911.httpchat.servlet.model;

import com.home911.httpchat.model.Presence;

public class Contact {
    private final Long id;
    private final String name;
    private final Presence presence;

    public Contact(Long id, String name, Presence presence) {
        this.id = id;
        this.name = name;
        this.presence = presence;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Presence getPresence() {
        return presence;
    }
}
