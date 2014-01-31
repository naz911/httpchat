package com.home911.httpchat.shared.model;

import java.io.Serializable;

public class Profile implements Serializable {
    private Long id;
    private String fullname;

    public Profile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Profile{");
        sb.append("id=").append(id);
        sb.append(", fullname='").append(fullname).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
