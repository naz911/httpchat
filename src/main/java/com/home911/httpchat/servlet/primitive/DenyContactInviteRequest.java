package com.home911.httpchat.servlet.primitive;

public class DenyContactInviteRequest {
    private Long id;

    public DenyContactInviteRequest(Long id) {
        this.id = id;
    }

    public DenyContactInviteRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
