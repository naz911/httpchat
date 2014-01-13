package com.home911.httpchat.service.notification;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.googlecode.objectify.ObjectifyService;
import com.home911.httpchat.service.logic.BusinessLogic;
import com.home911.httpchat.service.logic.BusinessLogicImpl;
import com.home911.httpchat.transaction.Transaction;
import com.home911.httpchat.transaction.TransactionInterceptor;
import com.home911.httpchat.utils.UTCReadableInstantTranslatorFactory;

public class NotificationServiceGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NotificationService.class).to(NotificationServiceImpl.class).in(Singleton.class);
    }
}
