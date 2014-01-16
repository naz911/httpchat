package com.home911.httpchat.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;

@Entity
@Cache
public class User {
    @Id
    private Long id;
    @Index
    private String username;
    @Index
    private String password;
    @Index
    private String email;
    private Presence presence = Presence.OFFLINE;
    private String token;

    @Load
    private Ref<UserInfo> userInfo;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

    public UserInfo getUserInfo() {
        if (userInfo != null) {
            return userInfo.get();
        } else {
            return null;
        }
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = Ref.create(userInfo);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
