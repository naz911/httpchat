package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.model.StatusResult;
import com.home911.httpchat.shared.model.Profile;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(MenuView.class.getName());

    private final Window menuWindow;
    private final MainView mainView;

    private IButton loginBtn;
    private IButton logoutBtn;
    private IButton profileBtn;
    private IButton searchBtn;
    private IButton aboutBtn;

    public MenuView(MainView mainView) {
        this.mainView = mainView;
        this.menuWindow = new Window();
        menuWindow.setTitle("HttpChat Menu");
        menuWindow.setAutoSize(true);
        menuWindow.setAnimateMinimize(true);
        menuWindow.setCanDragResize(false);
        menuWindow.setShowCloseButton(false);
        menuWindow.setCanDragReposition(false);
        menuWindow.setCanDrag(false);
        menuWindow.setShowFooter(true);
        initWidget(menuWindow);

        addLoginMenu();
        addAboutMenu();
    }

    public void writeStatus(String status) {
        menuWindow.setStatus(status);
    }

    public void adjustAfterLogin(String token, Profile profile) {
        menuWindow.removeItem(loginBtn);
        menuWindow.removeItem(aboutBtn);
        addMyProfileMenu(token, profile);
        addSearchContactMenu(token);
        addLogoutMenu(token);
        addAboutMenu();
        menuWindow.draw();
    }

    private void adjustAfterLogout() {
        menuWindow.removeItem(profileBtn);
        menuWindow.removeItem(searchBtn);
        //menuWindow.removeItem(alertsBtn);
        menuWindow.removeItem(logoutBtn);
        menuWindow.removeItem(aboutBtn);
        addLoginMenu();
        addAboutMenu();
        menuWindow.draw();
    }

    private void addLoginMenu() {
        loginBtn = new IButton("Login");
        loginBtn.setShowRollOver(true);
        loginBtn.setShowDisabled(true);
        loginBtn.setShowDown(true);
        loginBtn.setWidth(150);
        loginBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Login button clicked!...");
                }
                LoginView loginView = new LoginView(mainView);
                loginView.display();
            }
        });
        menuWindow.addItem(loginBtn);
    }

    private void addAboutMenu() {
        aboutBtn = new IButton("About");
        aboutBtn.setShowRollOver(true);
        aboutBtn.setShowDisabled(true);
        aboutBtn.setShowDown(true);
        aboutBtn.setWidth(150);
        aboutBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "About button clicked!...");
                }
                mainView.showAbout();
            }
        });
        menuWindow.addItem(aboutBtn);
    }

    private void addLogoutMenu(final String token) {
        logoutBtn = new IButton("Logout");
        logoutBtn.setShowRollOver(true);
        logoutBtn.setShowDisabled(true);
        logoutBtn.setShowDown(true);
        logoutBtn.setWidth(150);
        logoutBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Logout clicked!");
                }
                mainView.getBackendService().logout(
                        token, new AsyncCallback<StatusResult>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                        writeStatus("An unexpected error has occured.");
                    }

                    @Override
                    public void onSuccess(StatusResult statusResult) {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "Received statusResult:" + statusResult.toString());
                        }
                        writeStatus(statusResult.getStatus().getDescription());
                        mainView.logout();
                        adjustAfterLogout();
                    }
                });
            }
        });
        menuWindow.addItem(logoutBtn);
    }

    private void addSearchContactMenu(final String token) {
        searchBtn = new IButton("Search Contacts");
        searchBtn.setShowRollOver(true);
        searchBtn.setShowDisabled(true);
        searchBtn.setShowDown(true);
        searchBtn.setWidth(150);
        searchBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Search contacts clicked!");
                }
                mainView.displaySearch(token);
            }
        });
        menuWindow.addItem(searchBtn);
    }

    private void addMyProfileMenu(final String token, final Profile profile) {
        profileBtn = new IButton("My Profile");
        profileBtn.setShowRollOver(true);
        profileBtn.setShowDisabled(true);
        profileBtn.setShowDown(true);
        profileBtn.setWidth(150);
        profileBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "My profile clicked!");
                }
                mainView.displayProfile(profile, token);
            }
        });
        menuWindow.addItem(profileBtn);
    }
}
