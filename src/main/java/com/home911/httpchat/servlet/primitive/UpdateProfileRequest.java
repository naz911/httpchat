package com.home911.httpchat.servlet.primitive;

import com.home911.httpchat.servlet.model.Profile;

public class UpdateProfileRequest {
    private Profile profile;

    public UpdateProfileRequest(Profile profile) {
        this.profile = profile;
    }

    public UpdateProfileRequest() {
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
