package com.home911.httpchat.server.service.message;

import com.home911.httpchat.server.model.Message;
import com.home911.httpchat.server.model.User;

import java.util.List;

public interface MessageService {
    public Message getMessage(Long id);
    public List<Message> getMessages(User to);
    public void removeMessages(User owner, List<Message> messages);
    public void saveMessage(Message message);
}
