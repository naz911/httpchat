package com.home911.httpchat.servlet.primitive;

import com.home911.httpchat.servlet.model.ContactFilterType;

import java.util.EnumSet;

public class ContactSearchRequest {
    private EnumSet<ContactFilterType> filterTypes;
    private String filterValue;
    private int offset;
    private int limit;

    public ContactSearchRequest(EnumSet<ContactFilterType> filterTypes, String filterValue, int offset, int limit) {
        this.filterTypes = filterTypes;
        this.filterValue = filterValue;
        this.offset = offset;
        this.limit = limit;
    }

    public EnumSet<ContactFilterType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(EnumSet<ContactFilterType> filterTypes) {
        this.filterTypes = filterTypes;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
