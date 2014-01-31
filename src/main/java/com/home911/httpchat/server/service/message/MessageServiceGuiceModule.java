package com.home911.httpchat.server.service.message;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class MessageServiceGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MessageService.class).to(MessageServiceImpl.class).in(Singleton.class);
    }
}
