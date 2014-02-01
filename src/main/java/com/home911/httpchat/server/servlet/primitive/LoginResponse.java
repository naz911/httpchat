package com.home911.httpchat.server.servlet.primitive;

import com.home911.httpchat.shared.model.Contact;
import com.home911.httpchat.shared.model.Profile;

import java.util.List;

public class LoginResponse extends StatusResponse {

    private Profile profile;
    private List<Contact> contacts;
    private String channelToken;
    private transient String token;
    private transient Long userId;

    public LoginResponse(int code, String description, String token, Long userId, Profile profile, String channelToken) {
        super(code, description);
        this.token = token;
        this.userId = userId;
        this.profile = profile;
        this.channelToken = channelToken;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getChannelToken() {
        return channelToken;
    }

    public void setChannelToken(String channelToken) {
        this.channelToken = channelToken;
    }
}
