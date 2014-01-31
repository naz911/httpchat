package com.home911.httpchat.server.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;

import java.util.*;

@Entity
@Cache
public class UserInfo {
    @Id
    private Long id;
    @Parent
    private Key<User> owner;
    @Index
    private String fullname;

    private Set<Ref<User>> contacts;

    private Set<Ref<User>> pendingContacts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Key<User> getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = Key.create(owner);
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Set<Ref<User>> getContacts() {
        if (contacts == null)
        {
            return Collections.unmodifiableSet(new HashSet<Ref<User>>());
        }
        return contacts;
    }

    public void setContacts(Set<Ref<User>> contacts) {
        this.contacts = contacts;
    }

    public Set<Ref<User>> getPendingContacts() {
        if (pendingContacts == null)
        {
            return Collections.unmodifiableSet(new HashSet<Ref<User>>());
        }
        return pendingContacts;
    }

    public void setPendingContacts(Set<Ref<User>> pendingContact) {
        this.pendingContacts = pendingContact;
    }

    public void addPendingContact(User contact) {
        if (pendingContacts == null) {
            pendingContacts = new HashSet<Ref<User>>();
        }
        pendingContacts.add(Ref.create(contact));
    }

    public boolean isPendingContact(User contact) {
        if (pendingContacts != null) {
            return pendingContacts.contains(Ref.create(contact));
        } else {
            return false;
        }
    }

    public boolean isContact(User contact) {
        if (contacts != null) {
            return contacts.contains(Ref.create(contact));
        } else {
            return false;
        }
    }

    public void addContact(User user) {
        if (contacts == null) {
            contacts = new HashSet<Ref<User>>();
        }
        contacts.add(Ref.create(user));

        if (pendingContacts != null) {
            pendingContacts.remove(Ref.create(user));
        }
    }

    public void removeContact(User user) {
        if (contacts != null) {
            contacts.remove(Ref.create(user));
        }
    }
}
