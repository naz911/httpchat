package com.home911.httpchat.server.servlet.primitive;

public class ContactInviteRequest {
    private Long id;

    public ContactInviteRequest(Long id) {
        this.id = id;
    }

    public ContactInviteRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
