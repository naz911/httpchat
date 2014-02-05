package com.home911.httpchat.client.notif;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.home911.httpchat.client.gui.MainView;
import com.home911.httpchat.client.gui.MessageView;
import com.home911.httpchat.client.model.PollResult;
import com.home911.httpchat.shared.model.Alert;
import com.home911.httpchat.shared.model.Contact;
import com.home911.httpchat.shared.model.Message;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpChatNotifPollHandler extends Timer implements HttpChatNotifHandler {
    private static final Logger LOGGER = Logger.getLogger(HttpChatNotifPollHandler.class.getName());
    private static final int RUN_DELAY = 30 * 1000;

    private Long userId;
    private String token;
    private final MainView mainView;

    private int failureCount = 0;
    private boolean isStop = false;

    public HttpChatNotifPollHandler(MainView mainView) {
        this.mainView = mainView;
    }

    public void start(Map<String, Object> params) {
        this.userId = (Long) params.get(USERID_PARAM);
        this.token = (String) params.get(TOKEN_PARAM);
        this.isStop = false;
        schedule(RUN_DELAY);
    }

    public void stop() {
        this.isStop = true;
    }

    @Override
    public void run() {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Timer started...");
        }
        this.mainView.getBackendService().poll(userId, token, new AsyncCallback<PollResult>() {

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                failureCount++;
                scheduleNext();
            }

            @Override
            public void onSuccess(PollResult pollResult) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Timer succeed:" + pollResult.toString());
                }
                if (!isStop) {
                    if (pollResult.getStatus().getCode() == 200) {
                        failureCount = 0;
                        if (pollResult.getAlerts() != null) {
                            for (Alert alert : pollResult.getAlerts()) {
                                switch (alert.getType()) {
                                    case CONTACT_INVITE:
                                        if (LOGGER.isLoggable(Level.INFO)) {
                                            LOGGER.log(Level.INFO, "Got a Contact_Invite...");
                                        }
                                        mainView.getAlertView().addAlert(alert);
                                        break;
                                    case PRESENCE:
                                        if (LOGGER.isLoggable(Level.INFO)) {
                                            LOGGER.log(Level.INFO, "Presence update:" + alert.toString());
                                        }
                                        mainView.getContactListView().updateContactInList((Contact) alert.getData());
                                        if (LOGGER.isLoggable(Level.INFO)) {
                                            LOGGER.log(Level.INFO, "Presence updated!");
                                        }
                                        break;
                                }
                            }
                        }
                        if (pollResult.getMessages() != null) {
                            for (Message message : pollResult.getMessages()) {
                                if (LOGGER.isLoggable(Level.INFO)) {
                                    LOGGER.log(Level.INFO, "Message received:" + message.toString());
                                    MessageView msgView = mainView.getConversation(userId, token,
                                            message.getFrom().getId(),
                                            message.getFrom().getName());
                                    msgView.messageReceive(message);
                                }
                            }
                        }
                    } else {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, pollResult.getStatus().getDescription());
                        }
                        if (pollResult.getStatus().getCode() != 204) {
                            failureCount++;
                        }
                    }
                    scheduleNext();
                }
            }

            public void scheduleNext() {
                if (failureCount < 5) {
                    if (!isStop) {
                        schedule(RUN_DELAY);
                    } else {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "Timer stop as per request...");
                        }
                    }
                } else {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Timer stop reschedulling, too many failure!");
                    }
                }
            }
        });
    }
}
