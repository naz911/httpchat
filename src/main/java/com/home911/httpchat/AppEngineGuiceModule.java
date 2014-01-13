package com.home911.httpchat;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.BaseDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.quota.QuotaService;
import com.google.appengine.api.quota.QuotaServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.inject.AbstractModule;

public class AppEngineGuiceModule extends AbstractModule {
    private MailService mailService;

    private DatastoreService datastoreService;
    private AsyncDatastoreService asyncDatastoreService;

    private UserService userService;
    private OAuthService oauthService;
    private XMPPService xmppService;
    private QuotaService quotaService;
    private MemcacheService memcacheService;
    private ChannelService channelService;

    private AppEngineGuiceModule()
    {
    }

    @Override
    protected void configure()
    {
        if (memcacheService != null)
        {
            bind(MemcacheService.class).toInstance(memcacheService);
        }

        if (quotaService != null)
        {
            bind(QuotaService.class).toInstance(quotaService);
        }

        if (xmppService != null)
        {
            bind(XMPPService.class).toInstance(xmppService);
        }

        if (userService != null)
        {
            bind(UserService.class).toInstance(userService);
        }

        if (oauthService != null)
        {
            bind(OAuthService.class).toInstance(oauthService);
        }

        if (datastoreService != null)
        {
            bind(BaseDatastoreService.class).toInstance(datastoreService);
            bind(DatastoreService.class).toInstance(datastoreService);
        }

        if (asyncDatastoreService != null)
        {
            bind(BaseDatastoreService.class).toInstance(asyncDatastoreService);
            bind(AsyncDatastoreService.class).toInstance(asyncDatastoreService);
        }

        if (mailService != null)
        {
            bind(MailService.class).toInstance(mailService);
        }

        if (channelService != null)
        {
            bind(ChannelService.class).toInstance(channelService);
        }
    }

    public AppEngineGuiceModule withMemcacheService()
    {
        this.memcacheService = MemcacheServiceFactory.getMemcacheService();
        return this;
    }

    /**
     * BaseDatastoreService binds will be done according to last invocation of
     * this method or withAsyncDatastoreService
     *
     * @return
     */
    public AppEngineGuiceModule withDatastoreService()
    {
        this.datastoreService = DatastoreServiceFactory.getDatastoreService();
        this.asyncDatastoreService = null;
        return this;
    }

    /**
     * BaseDatastoreService binds will be done according to last invocation of
     * this method or withDatastoreService
     *
     * @return
     */
    public AppEngineGuiceModule withAsyncDatastoreService()
    {
        this.datastoreService = null;
        this.asyncDatastoreService = DatastoreServiceFactory.getAsyncDatastoreService();
        return this;
    }

    public AppEngineGuiceModule withQuotaService()
    {
        this.quotaService = QuotaServiceFactory.getQuotaService();
        return this;
    }

    public AppEngineGuiceModule withUserService()
    {
        this.userService = UserServiceFactory.getUserService();
        return this;
    }

    public AppEngineGuiceModule withXMPPService()
    {
        this.xmppService = XMPPServiceFactory.getXMPPService();
        return this;
    }

    public AppEngineGuiceModule withOAuthService()
    {
        this.oauthService = OAuthServiceFactory.getOAuthService();
        return this;
    }

    public AppEngineGuiceModule withMailService()
    {
        this.mailService = MailServiceFactory.getMailService();
        return this;
    }

    public AppEngineGuiceModule withChannelService()
    {
        this.channelService = ChannelServiceFactory.getChannelService();
        return this;
    }

    public static final AppEngineGuiceModule build()
    {
        return new AppEngineGuiceModule();
    }
}
