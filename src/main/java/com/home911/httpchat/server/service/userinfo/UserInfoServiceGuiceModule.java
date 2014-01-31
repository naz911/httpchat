package com.home911.httpchat.server.service.userinfo;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class UserInfoServiceGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserInfoService.class).to(UserInfoServiceImpl.class).in(Singleton.class);
    }
}
