package com.home911.httpchat.server.service.email;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.home911.httpchat.server.service.message.MessageService;
import com.home911.httpchat.server.service.message.MessageServiceImpl;

public class EmailServiceGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EmailService.class).to(EmailServiceImpl.class).in(Singleton.class);
    }
}
