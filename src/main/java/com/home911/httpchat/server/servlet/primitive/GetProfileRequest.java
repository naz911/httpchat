package com.home911.httpchat.server.servlet.primitive;

import com.home911.httpchat.shared.model.ProfileFilterType;

public class GetProfileRequest {
    private Long id;
    private ProfileFilterType filter;

    public GetProfileRequest(Long id, ProfileFilterType filter) {
        this.id = id;
        this.filter = filter;
    }

    public GetProfileRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProfileFilterType getFilter() {
        return filter;
    }

    public void setFilter(ProfileFilterType filter) {
        this.filter = filter;
    }
}
