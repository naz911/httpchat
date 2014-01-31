package com.home911.httpchat.server.model;

public enum ContactSearchFilterField {
    USERNAME("username"), EMAIL("email"), FULLNAME("userInfo.fullname");

    private final String filter;

    private ContactSearchFilterField(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }
}
