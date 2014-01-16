package com.home911.httpchat.model;

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

    private Set<Long> pendingContactIds;

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
        return contacts;
    }

    public void setContacts(Set<Ref<User>> contacts) {
        this.contacts = contacts;
    }

    public Set<Long> getPendingContactIds() {
        return pendingContactIds;
    }

    public void setPendingContactIds(Set<Long> pendingContactIds) {
        this.pendingContactIds = pendingContactIds;
    }

    public void addPendingContact(Long id) {
        if (pendingContactIds == null) {
            pendingContactIds = new HashSet<Long>();
        }
        pendingContactIds.add(id);
    }

    public void addContact(User user) {
        if (contacts == null) {
            contacts = new HashSet<Ref<User>>();
        }
        contacts.add(Ref.create(user));

        if (pendingContactIds != null) {
            pendingContactIds.remove(user.getId());
        }
    }
}
