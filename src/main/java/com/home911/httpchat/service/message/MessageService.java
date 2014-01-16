package com.home911.httpchat.service.message;

import com.home911.httpchat.model.Message;
import com.home911.httpchat.model.User;

import java.util.List;

public interface MessageService {
    public Message getMessage(Long id);
    public List<Message> getMessages(User to);
    public void removeMessages(List<Message> messages);
    public void saveMessage(Message message);
}
