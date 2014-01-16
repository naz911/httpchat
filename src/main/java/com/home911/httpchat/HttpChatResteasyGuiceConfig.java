package com.home911.httpchat;

import com.google.inject.AbstractModule;
import com.home911.httpchat.service.logic.BusinessLogicServiceGuiceModule;
import com.home911.httpchat.service.message.MessageServiceGuiceModule;
import com.home911.httpchat.service.notification.NotificationServiceGuiceModule;
import com.home911.httpchat.service.user.UserServiceGuiceModule;
import com.home911.httpchat.service.userinfo.UserInfoServiceGuiceModule;
import com.home911.httpchat.servlet.HttpChatGuiceServletModule;

public class HttpChatResteasyGuiceConfig extends AbstractModule {
    @Override
    protected void configure() {
        install(AppEngineGuiceModule.build().withUserService().withMailService());
        install(new UserServiceGuiceModule());
        install(new UserInfoServiceGuiceModule());
        install(new NotificationServiceGuiceModule());
        install(new MessageServiceGuiceModule());
        install(new BusinessLogicServiceGuiceModule());
        install(new HttpChatGuiceServletModule());
    }
}