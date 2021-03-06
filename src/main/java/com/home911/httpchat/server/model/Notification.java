package com.home911.httpchat.server.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import com.home911.httpchat.shared.model.NotificationType;

@Entity
@Cache
public class Notification {
    @Id
    private Long id;
    @Parent
    private Key<User> owner;
    @Index
    private Ref<User> referer;
    private NotificationType type;
    private Object data;

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

    public User getReferer() {
        return referer.get();
    }

    public void setReferer(User referer) {
        this.referer = Ref.create(referer);
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
