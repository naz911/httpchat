package com.home911.httpchat.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.home911.httpchat.HttpChatExceptionsMapper;
import com.home911.httpchat.servlet.resource.*;
import com.home911.httpchat.utils.GsonMessageBodyHandler;
import com.home911.httpchat.utils.SecurityInterceptor;


public class HttpChatGuiceServletModule extends AbstractModule {

    @Override
    protected void configure() {
        // Exception mapping binding
        bind(HttpChatExceptionsMapper.AllExceptionMapper.class).in(Singleton.class);
        bind(HttpChatExceptionsMapper.WebApplicationExceptionMapper.class).in(Singleton.class);
        bind(HttpChatExceptionsMapper.HttpChatExceptionMapper.class).in(Singleton.class);

        bind(GsonMessageBodyHandler.class).in(Singleton.class);
        bind(SecurityInterceptor.class).in(Singleton.class);

        // Resource mapping binding
        bind(RegisterResource.class).in(Singleton.class);
        bind(LoginResource.class).in(Singleton.class);
        bind(LogoutResource.class).in(Singleton.class);
        bind(ProfileResource.class).in(Singleton.class);
        bind(ContactResource.class).in(Singleton.class);
        bind(ContactsResource.class).in(Singleton.class);
        bind(AlertsResource.class).in(Singleton.class);
        bind(MessageResource.class).in(Singleton.class);
    }
}
