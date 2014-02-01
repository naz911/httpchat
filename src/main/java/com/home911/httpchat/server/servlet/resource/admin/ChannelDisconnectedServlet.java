package com.home911.httpchat.server.servlet.resource.admin;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.home911.httpchat.server.AppEngineGuiceModule;
import com.home911.httpchat.server.model.User;
import com.home911.httpchat.server.service.user.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChannelDisconnectedServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ChannelDisconnectedServlet.class.getCanonicalName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Receive channel disconnected...");
        }
        ChannelService channelService = AdminServletGuiceModule.getInstance().getService(ChannelService.class);
        ChannelPresence presence = channelService.parsePresence(req);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Client[" + presence.clientId() + "] disconnected...");
        }
        UserService userService = AdminServletGuiceModule.getInstance().getService(UserService.class);
        User user = userService.getUser(Long.parseLong(presence.clientId()));

        if (user != null) {
            user.setChannelConnected(false);
            userService.saveUser(user);
        }
        super.doPost(req, resp);
    }
}
