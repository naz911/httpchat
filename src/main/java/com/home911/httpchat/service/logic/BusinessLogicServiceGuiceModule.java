package com.home911.httpchat.service.logic;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.googlecode.objectify.ObjectifyService;
import com.home911.httpchat.transaction.Transaction;
import com.home911.httpchat.transaction.TransactionInterceptor;
import com.home911.httpchat.utils.UTCReadableInstantTranslatorFactory;

public class BusinessLogicServiceGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        ObjectifyService.factory().getTranslators().add(new UTCReadableInstantTranslatorFactory());

        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transaction.class), new TransactionInterceptor());
        bind(BusinessLogic.class).to(BusinessLogicImpl.class).in(Singleton.class);
    }
}
