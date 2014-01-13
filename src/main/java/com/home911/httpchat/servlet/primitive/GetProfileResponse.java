package com.home911.httpchat.servlet.primitive;

import com.home911.httpchat.servlet.model.Profile;

public class GetProfileResponse extends StatusResponse {
    private final Profile profile;

    public GetProfileResponse(int code, String description, Profile profile) {
        super(code, description);
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }
}
