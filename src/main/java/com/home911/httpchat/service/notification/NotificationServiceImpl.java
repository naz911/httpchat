package com.home911.httpchat.service.notification;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.home911.httpchat.model.Notification;
import com.home911.httpchat.model.User;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class NotificationServiceImpl implements NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationServiceImpl.class.getCanonicalName());

    public NotificationServiceImpl() {
        ObjectifyService.register(Notification.class);
    }

    @Override
    public Notification getNotification(User owner, Long id) {
        LOGGER.info("Getting notification for id:[" + id + "]");
        Notification notification = ofy().load().type(Notification.class).parent(owner).id(id).now();
        return notification;
    }

    @Override
    public List<Notification> getNotifications(User owner) {
        LOGGER.info("Getting notifications for owner[" + owner + "]");
        List<Notification> notifications = new ArrayList<Notification>();
        Query<Notification> query = ofy().load().type(Notification.class).ancestor(owner);
        QueryResultIterator<Notification> iterator = query.iterator();
        while (iterator.hasNext()) {
            notifications.add(iterator.next());
        }

        return notifications;
    }

    @Override
    public void removeNotifications(User owner, List<Notification> notifications) {
        LOGGER.info("Removing notifications [" + notifications + "]");
        //ofy().delete().entities(new Notification[notifications.size()]).now();
        List<Long> ids = new ArrayList<Long>(notifications.size());
        for (Notification notification : notifications) {
            ids.add(notification.getId());
        }

        ofy().delete().type(Notification.class).parent(owner).ids(ids);
    }

    @Override
    public void addNotifications(List<Notification> notifications) {
        LOGGER.info("Adding notifications [" + notifications + "]");
        ofy().save().entities(notifications.toArray(new Notification[notifications.size()])).now();
    }
}
