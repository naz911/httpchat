package com.home911.httpchat.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Message {
    @Id
    private Long id;
    @Index
    private Key<User> to;
    @Index
    private Ref<User> from;
    private String text;

    public Message() {
    }

    public Message(User to, User from, String text) {
        setTo(to);
        setFrom(from);
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Key<User> getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = Key.create(to);
    }

    public User getFrom() {
        return from.getValue();
    }

    public void setFrom(User from) {
        this.from = Ref.create(from);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
