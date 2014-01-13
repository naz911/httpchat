package com.home911.httpchat.service.user;

import com.home911.httpchat.model.ContactSearchFilterField;
import com.home911.httpchat.model.User;

import java.util.EnumSet;
import java.util.List;

public interface UserService {
    public User getUser(String username, String password);
    public boolean exists(String username);
    public User getUser(Long id);
    public void saveUser(User user);
    public List<User> getUsers(EnumSet<ContactSearchFilterField> filters, Object value, int offset, int limit);
}
