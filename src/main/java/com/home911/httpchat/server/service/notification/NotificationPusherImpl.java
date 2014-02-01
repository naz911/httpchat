package com.home911.httpchat.server.service.notification;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.inject.Inject;
import com.home911.httpchat.server.model.Notification;
import com.home911.httpchat.server.model.User;
import com.home911.httpchat.server.utils.GsonUtil;
import com.home911.httpchat.shared.model.Alert;
import com.home911.httpchat.shared.model.Contact;
import com.home911.httpchat.shared.model.Push;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationPusherImpl implements NotificationPusher {
    private static final Logger LOGGER = Logger.getLogger(NotificationPusherImpl.class.getCanonicalName());

    private final ChannelService channelService;

    @Inject
    public NotificationPusherImpl(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public String createChannel(String clientId) {
        return channelService.createChannel(clientId);
    }

    @Override
    public void push(Notification notification) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Generation notification for:" + notification);
        }
        Push push = convertNotification(notification);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Generation notification for:" + push);
        }
        String jsonPush = GsonUtil.getInstance().getGson().toJson(push);
        String destination = String.valueOf(notification.getOwner().getId());
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Pushing notification for:" + push);
        }
        channelService.sendMessage(new ChannelMessage(destination, jsonPush));
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Pushed notification!");
        }
    }

    @Override
    public void push(List<Notification> notifications) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Generation notifications for:" + notifications);
        }
        if (notifications != null && !notifications.isEmpty()) {
            for(Notification notif : notifications) {
                push(notif);
            }
        }
    }

    private Push convertNotification(Notification notification) {
        Push push = new Push();
        User contactUsr = notification.getReferer();
        String name = StringUtils.isEmpty(contactUsr.getUserInfo().getFullname()) ? contactUsr.getUsername() :
                contactUsr.getUserInfo().getFullname();
        push.setAlert(new Alert(notification.getId(), notification.getType(), notification.getReferer().getId(),
                new Contact(contactUsr.getId(), name, contactUsr.getPresence())));
        return push;
    }
}
