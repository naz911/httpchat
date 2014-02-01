package com.home911.httpchat.client.notif;

import java.util.Map;

public interface HttpChatNotifHandler {
    public static final String USERID_PARAM = "user.id";
    public static final String TOKEN_PARAM = "user.token";
    public static final String CHANNEL_TOKEN_PARAM = "channel.token";

    public void start(Map<String, Object> params);
    public void stop();
}
