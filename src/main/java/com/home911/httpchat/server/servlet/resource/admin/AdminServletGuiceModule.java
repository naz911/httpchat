package com.home911.httpchat.server.servlet.resource.admin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.home911.httpchat.server.AppEngineGuiceModule;
import com.home911.httpchat.server.service.user.UserServiceGuiceModule;

public class AdminServletGuiceModule {
    private static final AdminServletGuiceModule INSTANCE = new AdminServletGuiceModule();

    private final Injector injector;

    private AdminServletGuiceModule() {
        injector = Guice.createInjector(AppEngineGuiceModule.build().withChannelService(), new UserServiceGuiceModule());
    }

    static AdminServletGuiceModule getInstance() {
        return INSTANCE;
    }

    <T> T getService(Class<T> service) {
        return injector.getInstance(service);
    }
}
