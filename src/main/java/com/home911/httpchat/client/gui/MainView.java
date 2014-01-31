package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.service.BackendServiceClient;
import com.home911.httpchat.client.service.BackendServiceClientImpl;
import com.smartgwt.client.widgets.Canvas;

import java.util.logging.Logger;

public class MainView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(MainView.class.getName());

    private final BackendServiceClientImpl service;
    private final MenuView menuView;

    private ContactListView contactListView;
    private AlertView alertsView;
    private HttpChatTimer timer;

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

    public void login(Long userId, String token) {
        contactListView = new ContactListView(this, userId, token);
        alertsView = new AlertView(this, userId, token);
        startPolling(userId, token);
    }

    public void logout() {
        contactListView.hide();
        alertsView.hide();
        stopPolling();
    }

    private void startPolling(Long userId, String token) {
        this.timer = new HttpChatTimer(this);
        this.timer.start(userId, token);
    }

    private void stopPolling() {
        if (this.timer != null) {
            this.timer.stop();
        }
    }
}
