package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.notif.HttpChatNotifHandler;
import com.home911.httpchat.client.notif.HttpChatNotifPollHandler;
import com.home911.httpchat.client.notif.HttpChatNotifPushHandler;
import com.home911.httpchat.client.service.BackendServiceClient;
import com.home911.httpchat.client.service.BackendServiceClientImpl;
import com.home911.httpchat.shared.model.Profile;
import com.smartgwt.client.widgets.Canvas;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(MainView.class.getName());

    private final BackendServiceClientImpl service;
    private final MenuView menuView;
    private final AboutView aboutView;
    private final LoadingView loadingView;
    private SearchContactView searchContactsView;

    private ContactListView contactListView;
    private AlertView alertsView;
    private LoginView loginView;
    private HttpChatNotifHandler notifHandler;
    private Map<Long, MessageView> conversations = new HashMap<Long, MessageView>();

    private final Canvas canvas;

    public MainView(BackendServiceClientImpl service) {
        this.service = service;
        this.loadingView = new LoadingView();
        this.aboutView = new AboutView();
        this.canvas = new Canvas();
        //this.canvas.setWidth100();
        //this.canvas.setHeight100();
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

    public void showLoading(String label) {
        loadingView.display(label);
    }

    public void hideLoading() {
        loadingView.hide();
    }

    public void showAbout() {
        aboutView.display();
    }

    public void hideAbout() {
        aboutView.hide();
    }

    public void displayProfile(Profile profile, String token) {
        ProfileView profileView = new ProfileView(this, profile, token);
        profileView.display();
    }

    public void displaySearch(String token) {
        if (searchContactsView == null) {
            searchContactsView = new SearchContactView(this, token);
        }
        searchContactsView.display();
    }

    public void showConversation(String token, Long contactId, String contactName) {
        MessageView msgView = conversations.get(contactId);

        if (msgView == null) {
            msgView = new MessageView(this, contactName, contactId, token);
            conversations.put(contactId, msgView);
        }

        msgView.display();
    }

    public MessageView getConversation(String token, Long contactId, String contactName) {
        MessageView msgView = conversations.get(contactId);

        if (msgView == null) {
            msgView = new MessageView(this, contactName, contactId, token);
            conversations.put(contactId, msgView);
        }
        msgView.setTitle(contactName);

        return msgView;
    }

    public void disableConversation(Long contactId) {
        MessageView msgView = conversations.get(contactId);

        if (msgView != null) {
            msgView.disable();
        }
    }

    public void login(String token, String channelToken) {
        contactListView = new ContactListView(this, token);
        canvas.addChild(contactListView);
        alertsView = new AlertView(this, token);
        canvas.addChild(alertsView);
        if (channelToken != null && channelToken.trim().length() > 0) {
            //start the push channel
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Channel token detected, will open push channel...");
            }
            openChannel(token, channelToken);
        } else {
            LOGGER.log(Level.INFO, "Channel token not present, will start polling...");
            //start the poll channel
            startPolling(token);
        }
    }

    public void logout() {
        stopNotifHandler();
        contactListView.hide();
        contactListView = null;
        alertsView.hide();
        alertsView = null;
        loginView = null;
        for (MessageView msgView : conversations.values()) {
            msgView.hide();
        }
        conversations.clear();
    }

    private void openChannel(String token, String channelToken) {
        this.notifHandler = new HttpChatNotifPushHandler(this);
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put(HttpChatNotifHandler.TOKEN_PARAM, token);
        params.put(HttpChatNotifHandler.CHANNEL_TOKEN_PARAM, channelToken);
        this.notifHandler.start(params);
    }

    private void startPolling(String token) {
        this.notifHandler = new HttpChatNotifPollHandler(this);
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put(HttpChatNotifHandler.TOKEN_PARAM, token);
        this.notifHandler.start(params);
    }

    private void stopNotifHandler() {
        if (this.notifHandler != null) {
            this.notifHandler.stop();
        }
    }

    public void showLogin() {
        if (loginView == null) {
            loginView = new LoginView(this);
        }
        loginView.display();
    }
}
