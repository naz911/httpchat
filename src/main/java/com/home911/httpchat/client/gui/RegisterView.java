package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.model.StatusResult;
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
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(RegisterView.class.getName());

    private final MainView mainView;
    private final Window registerWnd;
    private Label registerMsg;

    public RegisterView(MainView mainView) {
        this.mainView = mainView;
        registerWnd = new Window();
        registerWnd.setTitle("HttpChat Registration");
        registerWnd.centerInPage();
        registerWnd.setAutoSize(true);
        registerWnd.setCanDragResize(false);
        registerWnd.setShowCloseButton(true);
        registerWnd.setShowMaximizeButton(false);
        registerWnd.setShowMinimizeButton(false);
        registerWnd.setIsModal(true);
        registerWnd.setShowModalMask(true);
        initWidget(registerWnd);

        registerWnd.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
                hide();
            }
        });

        createRegisterWindow();
    }

    public void display() {
        registerWnd.show();
    }

    public void hide() {
        registerWnd.destroy();
    }

    private void createRegisterWindow() {
        Label details = new Label("Please enter your information in order to register to HttpChat!");
        details.setHeight(30);
        details.setPadding(10);
        details.setLayoutAlign(Alignment.CENTER);
        details.setLayoutAlign(VerticalAlignment.CENTER);
        details.setWrap(false);
        details.setAlign(Alignment.CENTER);
        registerWnd.addItem(details);

        registerMsg = new Label();
        registerMsg.setAutoFit(true);
        registerMsg.setHeight(30);
        registerMsg.setPadding(10);
        registerMsg.setLayoutAlign(Alignment.CENTER);
        registerMsg.setLayoutAlign(VerticalAlignment.CENTER);
        registerMsg.setWrap(false);
        registerMsg.setAlign(Alignment.CENTER);
        registerWnd.addItem(registerMsg);

        final DynamicForm form = new DynamicForm();
        form.setAutoFocus(true);
        form.setCanFocus(true);
        form.setWidth(300);
        form.setLayoutAlign(Alignment.CENTER);
        form.setLayoutAlign(VerticalAlignment.CENTER);

        final TextItem usernameItem = new TextItem();
        usernameItem.setTitle("Username");
        usernameItem.setRequired(true);

        final PasswordItem passwordItem = new PasswordItem();
        passwordItem.setTitle("Password");
        passwordItem.setRequired(true);

        final PasswordItem passwordConfirmItem = new PasswordItem();
        passwordConfirmItem.setTitle("Password again");
        passwordConfirmItem.setRequired(true);

        final TextItem emailItem = new TextItem();
        emailItem.setTitle("Email");
        emailItem.setRequired(true);

        emailItem.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent keyPressEvent) {
                if ("enter".equalsIgnoreCase(keyPressEvent.getKeyName())) {
                    processRegister(usernameItem, emailItem, passwordItem, passwordConfirmItem);
                }
            }
        });

        form.setFields(new FormItem[] {usernameItem, passwordItem, passwordConfirmItem, emailItem});
        registerWnd.addItem(form);

        HLayout buttons = new HLayout();
        buttons.setLayoutAlign(Alignment.CENTER);
        buttons.setHeight(30);
        registerWnd.addItem(buttons);
        IButton registerBtn = new IButton("Register");
        registerBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                processRegister(usernameItem, emailItem, passwordItem, passwordConfirmItem);
            }
        });
        buttons.addMember(registerBtn);
        IButton resetBtn = new IButton("Reset");
        resetBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.reset();
                registerMsg.setContents("");
            }
        });
        buttons.addMember(resetBtn);
    }

    private void processRegister(final TextItem usernameItem, final TextItem emailItem,
                                 final PasswordItem passwordItem, final PasswordItem passwordConfirmItem) {
        StringBuilder errorMsg = new StringBuilder("The following error(s) occured:<br/>");
        boolean isError = false;
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Form submitted with username[" + usernameItem.getEnteredValue().trim() + "]," +
                    " password[" + passwordItem.getEnteredValue().trim() + "]");
        }
        if (usernameItem.getEnteredValue().trim() == "") {
            isError = true;
            errorMsg.append("- Username field is empty<br/>");
        }
        if (passwordItem.getEnteredValue().trim() == "") {
            isError = true;
            errorMsg.append("- Password field is empty<br/>");
        }
        if (passwordConfirmItem.getEnteredValue().trim() == "") {
            isError = true;
            errorMsg.append("- Confirm Password field is empty<br/>");
        }
        if (emailItem.getEnteredValue().trim() == "") {
            isError = true;
            errorMsg.append("- Email Password field is empty<br/>");
        }
        if (!passwordItem.getEnteredValue().trim().equals(passwordConfirmItem.getEnteredValue().trim())) {
            isError = true;
            errorMsg.append("- Passwords does not match<br/>");
        }

        if (isError) {
            registerMsg.setContents(errorMsg.toString());
        } else {
            mainView.showLoading("Processing registration...");
            mainView.getBackendService().register(usernameItem.getEnteredValue().trim(),
                    passwordItem.getEnteredValue().trim(),
                    emailItem.getEnteredValue().trim(), new AsyncCallback<StatusResult>() {
                @Override
                public void onFailure(Throwable throwable) {
                    mainView.hideLoading();
                    LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                    registerMsg.setContents("An unexpected error has occured.");
                }

                @Override
                public void onSuccess(final StatusResult statusResult) {
                    mainView.hideLoading();
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Received statusResult:" + statusResult.toString());
                    }
                    if (statusResult.getStatus().getCode() == 200) {
                        mainView.getMenuView().writeStatus(statusResult.getStatus().getDescription());
                        hide();
                    } else {
                        registerMsg.setContents(statusResult.getStatus().getDescription());
                    }
                }
            });
        }
    }
}
