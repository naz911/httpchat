package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.home911.httpchat.client.model.PollResult;
import com.home911.httpchat.shared.model.Alert;
import com.home911.httpchat.shared.model.Contact;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpChatTimer extends Timer {
    private static final Logger LOGGER = Logger.getLogger(HttpChatTimer.class.getName());
    private static final int RUN_DELAY = 30 * 1000;

    private Long userId;
    private String token;
    private final MainView mainView;

    private int failureCount = 0;
    private boolean isStop = false;

    public HttpChatTimer(MainView mainView) {
        this.mainView = mainView;
    }

    public void start(Long userId, String token) {
        this.userId = userId;
        this.token = token;
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
