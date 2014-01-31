package com.home911.httpchat.server.service.user;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class UserServiceGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
    }
}
