package com.home911.httpchat.server.service.email;

import com.google.appengine.api.mail.MailService;
import com.google.inject.Inject;
import com.home911.httpchat.server.exception.ServerErrorException;
import com.home911.httpchat.server.model.User;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getCanonicalName());
    private static final String NO_REPLY_SENDER = "no-reply@myhttprestchat.appspot.com";
    private static final String CONFIRM_REGISTRATION_SUBJECT = "HttpChat Confirm Registration";
    private static final String CONFIRM_REGISTRATION_TEXT_BODY = "Hi {0},\r\n\r\nWelcome to HttpChat, please confirm your email address by going to the following link:\r\n{1}\r\n\r\nThanks and see you soon!";

    private final MailService mailService;

    @Inject
    public EmailServiceImpl(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendRegistrationEmail(User user, String confirmUrl) {
        MailService.Message email = new MailService.Message();
        email.setTo(user.getEmail());
        email.setSender(NO_REPLY_SENDER);
        email.setSubject(CONFIRM_REGISTRATION_SUBJECT);
        email.setTextBody(MessageFormat.format(CONFIRM_REGISTRATION_TEXT_BODY, user.getUsername(), confirmUrl));
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Sending confirm registration email:" + email.toString());
        }
        try {
            mailService.send(email);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception.", e);
            throw new ServerErrorException();
        }
    }
}
