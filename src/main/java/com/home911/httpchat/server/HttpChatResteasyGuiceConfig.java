package com.home911.httpchat.server;

import com.google.inject.AbstractModule;
import com.home911.httpchat.server.service.email.EmailServiceGuiceModule;
import com.home911.httpchat.server.service.logic.BusinessLogicServiceGuiceModule;
import com.home911.httpchat.server.service.message.MessageServiceGuiceModule;
import com.home911.httpchat.server.service.notification.NotificationServiceGuiceModule;
import com.home911.httpchat.server.service.user.UserServiceGuiceModule;
import com.home911.httpchat.server.service.userinfo.UserInfoServiceGuiceModule;
import com.home911.httpchat.server.servlet.HttpChatGuiceServletModule;

public class HttpChatResteasyGuiceConfig extends AbstractModule {
    @Override
    protected void configure() {
        install(AppEngineGuiceModule.build().withMailService());
        install(new UserServiceGuiceModule());
        install(new UserInfoServiceGuiceModule());
        install(new NotificationServiceGuiceModule());
        install(new MessageServiceGuiceModule());
        install(new EmailServiceGuiceModule());
        install(new BusinessLogicServiceGuiceModule());
        install(new HttpChatGuiceServletModule());
    }
}