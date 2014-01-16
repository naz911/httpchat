package com.home911.httpchat.service.message;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.home911.httpchat.model.Message;
import com.home911.httpchat.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class MessageServiceImpl implements MessageService {
    private static final Logger LOGGER = Logger.getLogger(MessageServiceImpl.class.getCanonicalName());

    public MessageServiceImpl() {
        ObjectifyService.register(Message.class);
    }

    @Override
    public Message getMessage(Long id){
        LOGGER.info("Getting message for id:[" + id + "]");
        Message message = ofy().load().type(Message.class).id(id).now();
        return message;
    }

    @Override
    public List<Message> getMessages(User to) {
        LOGGER.info("Getting messages for owner[" + to + "]");
        List<Message> messages = new ArrayList<Message>();
        Query<Message> query = ofy().load().type(Message.class).ancestor(to);
        QueryResultIterator<Message> iterator = query.iterator();
        while (iterator.hasNext()) {
            messages.add(iterator.next());
        }

        return messages;
    }

    @Override
    public void removeMessages(User owner, List<Message> messages) {
        LOGGER.info("Removing messages [" + messages + "]");
        //ofy().delete().entities(new Notification[notifications.size()]).now();
        List<Long> ids = new ArrayList<Long>(messages.size());
        for (Message message : messages) {
            ids.add(message.getId());
        }

        ofy().delete().type(Message.class).parent(owner).ids(ids);
    }

    @Override
    public void saveMessage(Message message) {
        LOGGER.info("Saving message [" + message + "]");
        ofy().save().entity(message).now();
    }
}
