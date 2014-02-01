package com.home911.httpchat.server.service.notification;

import com.home911.httpchat.server.model.Notification;

import java.util.List;

public interface NotificationPusher {
    public String createChannel(String clientId);
    public void push(List<Notification> notifications);
    public void push(Notification notification);
}
