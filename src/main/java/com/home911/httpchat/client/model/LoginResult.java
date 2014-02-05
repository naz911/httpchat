package com.home911.httpchat.client.model;

import com.home911.httpchat.shared.model.Alert;
import com.home911.httpchat.shared.model.Contact;
import com.home911.httpchat.shared.model.Profile;

import java.util.List;

public class LoginResult extends StatusResult {
    private Long userId;
    private String token;
    private String channelToken;
    private Profile profile;
    private List<Contact> contacts;
    private List<Alert> alerts;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getChannelToken() {
        return channelToken;
    }

    public void setChannelToken(String channelToken) {
        this.channelToken = channelToken;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoginResult{");
        sb.append("userId=").append(userId);
        sb.append(", token='").append(token).append('\'');
        sb.append(", channelToken='").append(channelToken).append('\'');
        sb.append(", profile=").append(profile);
        sb.append(", contacts=").append(contacts);
        sb.append(", alerts=").append(alerts);
        sb.append('}');
        return sb.toString();
    }
}
