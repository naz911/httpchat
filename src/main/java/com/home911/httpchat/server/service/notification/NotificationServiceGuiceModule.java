package com.home911.httpchat.server.service.notification;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class NotificationServiceGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NotificationService.class).to(NotificationServiceImpl.class).in(Singleton.class);
    }
}
