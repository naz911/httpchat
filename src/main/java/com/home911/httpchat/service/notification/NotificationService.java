package com.home911.httpchat.service.notification;

import com.home911.httpchat.model.Notification;
import com.home911.httpchat.model.User;

import java.util.List;

public interface NotificationService {
    public Notification getNotification(Long id);
    public List<Notification> getNotifications(User owner);
    public void removeNotifications(List<Notification> notifications);
    public void addNotifications(List<Notification> notifications);
}
