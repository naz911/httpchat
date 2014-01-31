package com.home911.httpchat.server.service.email;

import com.home911.httpchat.server.model.User;

public interface EmailService {
    public void sendRegistrationEmail(User user, String confirmUrl);
}
