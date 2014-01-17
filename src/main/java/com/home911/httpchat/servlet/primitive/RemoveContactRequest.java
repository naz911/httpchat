package com.home911.httpchat.servlet.primitive;

public class RemoveContactRequest {
    private Long id;

    public RemoveContactRequest(Long id) {
        this.id = id;
    }

    public RemoveContactRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
