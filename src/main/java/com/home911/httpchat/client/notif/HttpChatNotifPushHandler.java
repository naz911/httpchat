package com.home911.httpchat.client.notif;

import com.google.gwt.appengine.channel.client.*;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.home911.httpchat.client.gui.MainView;
import com.home911.httpchat.client.gui.MessageView;
import com.home911.httpchat.client.utils.ParserUtil;
import com.home911.httpchat.shared.model.Contact;
import com.home911.httpchat.shared.model.Presence;
import com.home911.httpchat.shared.model.Push;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpChatNotifPushHandler implements HttpChatNotifHandler {
    private static final Logger LOGGER = Logger.getLogger(HttpChatNotifPushHandler.class.getName());

    private final MainView mainView;
    private String channelToken;
    private String token;
    private Channel channel;
    private Socket socket;

    public HttpChatNotifPushHandler(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void start(Map<String, Object> params) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Starting Push channel...");
        }
        this.token = (String) params.get(TOKEN_PARAM);
        this.channelToken = (String) params.get(CHANNEL_TOKEN_PARAM);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Creating Channel...");
        }
        ChannelFactory.createChannel(this.channelToken, new ChannelFactory.ChannelCreatedCallback() {

            @Override
            public void onChannelCreated(Channel channel) {
                channel.open(new SocketListener() {
                    @Override
                    public void onOpen() {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "HttpChat Push Notification Channel connected...");
                        }
                    }

                    @Override
                    public void onMessage(String message) {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "HttpChat Push Notification Channel message received:" + message);
                        }
                        JSONValue jsonPushValue = JSONParser.parseStrict(message);
                        if (jsonPushValue != null) {
                            Push push = ParserUtil.parsePush(jsonPushValue.isObject());
                            if (push != null) {
                                if (push.getAlert() != null) {
                                    switch (push.getAlert().getType()) {
                                        case CONTACT_INVITE:
                                            if (LOGGER.isLoggable(Level.INFO)) {
                                                LOGGER.log(Level.INFO, "Got a Contact_Invite...");
                                            }
                                            mainView.getAlertView().addAlert(push.getAlert());
                                            break;
                                        case PRESENCE:
                                            if (LOGGER.isLoggable(Level.INFO)) {
                                                LOGGER.log(Level.INFO, "Presence update:" + push.getAlert().toString());
                                            }
                                            Contact contact = (Contact) push.getAlert().getData();
                                            mainView.getContactListView().updateContactInList(contact);
                                            if (LOGGER.isLoggable(Level.INFO)) {
                                                LOGGER.log(Level.INFO, "Presence updated!");
                                            }
                                            if (Presence.OFFLINE == contact.getPresence()) {
                                                mainView.disableConversation(contact.getId());
                                            }
                                            break;
                                    }
                                } else if (push.getMessage() != null) {
                                    if (LOGGER.isLoggable(Level.INFO)) {
                                        LOGGER.log(Level.INFO, "Message received:" + push.getMessage().toString());
                                    }
                                    MessageView msgView = mainView.getConversation(token,
                                            push.getMessage().getFrom().getId(),
                                            push.getMessage().getFrom().getName());
                                    msgView.messageReceive(push.getMessage());
                                }
                            } else {
                                LOGGER.log(Level.WARNING, "HttpChat Push message discarded:empty");
                            }
                        } else {
                            LOGGER.log(Level.WARNING, "HttpChat Push message discarded:unrecognize");
                        }
                    }

                    @Override
                    public void onError(SocketError socketError) {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "HttpChat Push Notification Channel error received:" + socketError.getDescription());
                        }
                        mainView.getMenuView().writeStatus("Push Channel error...");
                    }

                    @Override
                    public void onClose() {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "HttpChat Push Notification Channel closed...");
                        }
                        mainView.getMenuView().writeStatus("Push Channel closed...");
                    }
                });
            }
        });
    }

    @Override
    public void stop() {
        LOGGER.log(Level.INFO, "Stopping Push channel...");
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        this.channel = null;
    }
}
