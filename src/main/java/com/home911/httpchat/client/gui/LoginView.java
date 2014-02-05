package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.model.LoginResult;
import com.home911.httpchat.shared.model.Alert;
import com.home911.httpchat.shared.model.NotificationType;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    private final MainView mainView;
    private final Window loginWnd;
    private Label loginMsg;

    public LoginView(MainView mainView) {
        this.mainView = mainView;
        loginWnd = new Window();
        loginWnd.setTitle("HttpChat Login");
        loginWnd.centerInPage();
        loginWnd.setTop(0);
        loginWnd.setAutoSize(true);
        loginWnd.setCanDragResize(false);
        loginWnd.setShowCloseButton(true);
        loginWnd.setShowMaximizeButton(false);
        loginWnd.setShowMinimizeButton(false);
        loginWnd.setIsModal(true);
        loginWnd.setShowModalMask(true);
        initWidget(loginWnd);

        loginWnd.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
                hide();
            }
        });
        createLoginWindow();
    }

    public void display() {
        loginWnd.show();
    }

    public void hide() {
        loginWnd.destroy();
    }

    private void createLoginWindow() {
        Label details = new Label("Please enter your username and password to login!");
        details.setHeight(30);
        details.setPadding(10);
        details.setLayoutAlign(Alignment.CENTER);
        details.setLayoutAlign(VerticalAlignment.CENTER);
        details.setWrap(false);
        details.setAlign(Alignment.CENTER);
        loginWnd.addItem(details);

        loginMsg = new Label();
        loginMsg.setAutoFit(true);
        loginMsg.setHeight(30);
        loginMsg.setPadding(10);
        loginMsg.setLayoutAlign(Alignment.CENTER);
        loginMsg.setLayoutAlign(VerticalAlignment.CENTER);
        loginMsg.setWrap(false);
        loginMsg.setAlign(Alignment.CENTER);
        loginWnd.addItem(loginMsg);

        final DynamicForm form = new DynamicForm();
        form.setAutoFocus(true);
        form.setCanFocus(true);
        form.setHeight100();
        form.setWidth100();
        form.setLayoutAlign(Alignment.CENTER);
        form.setLayoutAlign(VerticalAlignment.CENTER);

        final TextItem usernameItem = new TextItem();
        usernameItem.setTitle("Username");
        usernameItem.setRequired(true);

        final PasswordItem passwordItem = new PasswordItem();
        passwordItem.setTitle("Password");
        passwordItem.setRequired(true);

        final CheckboxItem usePushChk = new CheckboxItem();
        usePushChk.setTitle("Use push?");
        usePushChk.setDisabled(true);

        passwordItem.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent keyPressEvent) {
                if ("enter".equalsIgnoreCase(keyPressEvent.getKeyName())) {
                    processLogin(usernameItem, passwordItem, usePushChk);
                }
            }
        });

        form.setFields(new FormItem[] {usernameItem, passwordItem, usePushChk});
        loginWnd.addItem(form);

        HLayout buttons = new HLayout();
        buttons.setLayoutAlign(Alignment.CENTER);
        buttons.setHeight(30);
        loginWnd.addItem(buttons);
        final IButton loginBtn = new IButton("Login");
        loginBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                processLogin(usernameItem, passwordItem, usePushChk);
            }
        });
        buttons.addMember(loginBtn);
        final IButton resetBtn = new IButton("Reset");
        resetBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.reset();
                loginMsg.setContents("");
            }
        });
        buttons.addMember(resetBtn);

        HLayout registerLay = new HLayout();
        registerLay.setLayoutAlign(Alignment.CENTER);
        registerLay.setHeight(30);
        loginWnd.addItem(registerLay);
        IButton registerBtn = new IButton("Not register yet?");
        registerBtn.setAutoFit(true);
        registerBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
                RegisterView resisterView = new RegisterView(mainView);
                resisterView.display();
            }
        });
        registerLay.addChild(registerBtn);
    }

    private void processLogin(final TextItem usernameItem, final PasswordItem passwordItem,
                              final CheckboxItem usePushChk) {
        mainView.getMenuView().writeStatus("");
        StringBuilder errorMsg = new StringBuilder("The following error(s) occured:<br/>");
        boolean isError = false;
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Form submitted with username[" + usernameItem.getEnteredValue().trim() + "]," +
                    " password[" + passwordItem.getEnteredValue().trim() + "]," +
                    "usePush[" + usePushChk.getValueAsBoolean() + "]");
        }
        if (usernameItem.getEnteredValue().trim() == "") {
            isError = true;
            errorMsg.append("- Username field is empty<br/>");
        }
        if (passwordItem.getEnteredValue().trim() == "") {
            isError = true;
            errorMsg.append("- Password field is empty<br/>");
        }

        if (isError) {
            loginMsg.setContents(errorMsg.toString());
        } else {
            mainView.showLoading("Processing loading...");
            mainView.getBackendService().login(usernameItem.getEnteredValue().trim(),
                    passwordItem.getEnteredValue().trim(), usePushChk.getValueAsBoolean(),
                    new AsyncCallback<LoginResult>() {
                @Override
                public void onFailure(Throwable throwable) {
                    mainView.hideLoading();
                    LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                    loginMsg.setContents("An unexpected error has occured.");
                }

                @Override
                public void onSuccess(final LoginResult loginResult) {
                    mainView.hideLoading();
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Received loginResult:" + loginResult.toString());
                    }
                    if (loginResult.getStatus().getCode() == 200) {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "loginResult is success.");
                        }
                        loginWnd.hide();
                        mainView.getMenuView().writeStatus(loginResult.getStatus().getDescription());
                        mainView.login(loginResult.getUserId(), loginResult.getToken(), loginResult.getChannelToken());
                        mainView.getMenuView().adjustAfterLogin(loginResult.getUserId(),
                                loginResult.getToken(), loginResult.getProfile());
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "Populating contacts");
                        }
                        mainView.getContactListView().populateContactList(loginResult.getContacts());

                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, "Populating contacts");
                        }
                        if (loginResult.getAlerts() != null) {
                            for (Alert alert : loginResult.getAlerts()) {
                                if (NotificationType.CONTACT_INVITE == alert.getType()) {
                                    if (LOGGER.isLoggable(Level.INFO)) {
                                        LOGGER.log(Level.INFO, "Got a Contact_Invite...");
                                    }
                                    mainView.getAlertView().addAlert(alert);
                                }
                            }
                        }
                    } else {
                        loginMsg.setContents(loginResult.getStatus().getDescription());
                    }
                }
            });
        }
    }
}
