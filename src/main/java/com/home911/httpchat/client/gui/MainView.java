package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.notif.HttpChatNotifHandler;
import com.home911.httpchat.client.notif.HttpChatNotifPollHandler;
import com.home911.httpchat.client.notif.HttpChatNotifPushHandler;
import com.home911.httpchat.client.service.BackendServiceClient;
import com.home911.httpchat.client.service.BackendServiceClientImpl;
import com.smartgwt.client.widgets.Canvas;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(MainView.class.getName());

    private final BackendServiceClientImpl service;
    private final MenuView menuView;

    private ContactListView contactListView;
    private AlertView alertsView;
    private HttpChatNotifHandler notifHandler;

    public MainView(BackendServiceClientImpl service) {
        this.service = service;
        Canvas canvas = new Canvas();
        this.menuView = new MenuView(this);

        canvas.addChild(this.menuView);
        canvas.draw();
    }

    public BackendServiceClient getBackendService() {
        return service;
    }

    public MenuView getMenuView() {
        return menuView;
    }

    public AlertView getAlertView() {
        return alertsView;
    }

    public ContactListView getContactListView() {
        return contactListView;
    }

    public void login(Long userId, String token, String channelToken) {
        contactListView = new ContactListView(this, userId, token);
        alertsView = new AlertView(this, userId, token);
        if (channelToken != null && channelToken.trim().length() > 0) {
            //start the push channel
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Channel token detected, will open push channel...");
            }
            openChannel(userId, token, channelToken);
        } else {
            LOGGER.log(Level.INFO, "Channel token not present, will start polling...");
            //start the poll channel
            startPolling(userId, token);
        }
    }

    public void logout() {
        contactListView.hide();
        alertsView.hide();
        stopNotifHandler();
    }

    private void openChannel(Long userId, String token, String channelToken) {
        this.notifHandler = new HttpChatNotifPushHandler(this);
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put(HttpChatNotifHandler.USERID_PARAM, userId);
        params.put(HttpChatNotifHandler.TOKEN_PARAM, token);
        params.put(HttpChatNotifHandler.CHANNEL_TOKEN_PARAM, channelToken);
        this.notifHandler.start(params);
    }

    private void startPolling(Long userId, String token) {
        this.notifHandler = new HttpChatNotifPollHandler(this);
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put(HttpChatNotifHandler.USERID_PARAM, userId);
        params.put(HttpChatNotifHandler.TOKEN_PARAM, token);
        this.notifHandler.start(params);
    }

    private void stopNotifHandler() {
        if (this.notifHandler != null) {
            this.notifHandler.stop();
        }
    }
}
