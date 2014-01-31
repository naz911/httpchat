package com.home911.httpchat.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.home911.httpchat.client.service.BackendServiceClientImpl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpChat implements EntryPoint {
    private static final String APP_NAME = "/httpchat";
    private static final Logger LOGGER = Logger.getLogger(HttpChat.class.getName());

    @Override
    public void onModuleLoad() {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "BaseURL=" + GWT.getHostPageBaseURL());
        }
        BackendServiceClientImpl backend = new BackendServiceClientImpl(stripAppName(GWT.getModuleBaseURL()));
        RootPanel.get().add(backend.getMainView());
    }

    private String stripAppName(String moduleBaseURL) {
        int pos = moduleBaseURL.indexOf(APP_NAME);
        if (pos >= 0) {
            return moduleBaseURL.substring(pos + APP_NAME.length());
        }
        return moduleBaseURL;
    }
}
