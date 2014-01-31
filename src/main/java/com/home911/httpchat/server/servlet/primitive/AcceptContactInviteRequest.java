package com.home911.httpchat.server.servlet.primitive;

public class AcceptContactInviteRequest {
    private Long id;

    public AcceptContactInviteRequest(Long id) {
        this.id = id;
    }

    public AcceptContactInviteRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
