package com.home911.httpchat.client.model;

import com.home911.httpchat.shared.model.Profile;

public class ProfileResult extends StatusResult {
    private Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProfileResult{");
        sb.append(super.toString()).append(",")
                .append("profile=").append(profile)
                .append('}');
        return sb.toString();
    }
}
