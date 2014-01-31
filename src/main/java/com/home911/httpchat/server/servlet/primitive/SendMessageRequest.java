package com.home911.httpchat.server.servlet.primitive;

import com.home911.httpchat.shared.model.Message;

public class SendMessageRequest {
    private Message message;

    public SendMessageRequest(Message message) {
        this.message = message;
    }

    public SendMessageRequest() {
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
