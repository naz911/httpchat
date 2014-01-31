package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.model.StatusResult;
import com.home911.httpchat.shared.model.Profile;
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
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(ProfileView.class.getName());

    private final MainView mainView;
    private final Window profileWnd;
    private Label profileMsg;

    private final Long userId;
    private final String token;
    private final Profile profile;

    public ProfileView(MainView mainView, Profile profile) {
        this(mainView, profile, null, null);
    }

    public ProfileView(MainView mainView, Profile profile, Long userId, String token) {
        this.mainView = mainView;
        this.userId = userId;
        this.token = token;
        this.profile = profile;
        profileWnd = new Window();
        profileWnd.setTitle("HttpChat Profile");
        profileWnd.centerInPage();
        profileWnd.setTop(0);
        profileWnd.setAutoSize(true);
        profileWnd.setCanDragResize(false);
        profileWnd.setShowCloseButton(true);
        profileWnd.setShowMaximizeButton(false);
        profileWnd.setShowMinimizeButton(false);
        profileWnd.setIsModal(true);
        profileWnd.setShowModalMask(true);
        initWidget(profileWnd);

        profileWnd.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
                hide();
            }
        });
        createProfileWindow();
    }

    public void display() {
        profileWnd.show();
    }

    public void hide() {
        profileWnd.destroy();
    }

    private void createProfileWindow() {
        profileMsg = new Label();
        profileMsg.setAutoFit(true);
        profileMsg.setHeight(30);
        profileMsg.setPadding(10);
        profileMsg.setLayoutAlign(Alignment.CENTER);
        profileMsg.setLayoutAlign(VerticalAlignment.CENTER);
        profileMsg.setWrap(false);
        profileMsg.setAlign(Alignment.CENTER);
        profileWnd.addItem(profileMsg);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Drawing profile form for profile[" + profile.toString() +
                    "], with userid[" + userId + "] and token[" + token + "]");
        }
        final DynamicForm form = new DynamicForm();
        form.setAutoFocus(true);
        form.setCanFocus(true);
        form.setHeight100();
        form.setWidth100();
        form.setLayoutAlign(Alignment.CENTER);
        form.setLayoutAlign(VerticalAlignment.CENTER);

        final TextItem fullnameItem = new TextItem();
        fullnameItem.setTitle("Fullname");
        fullnameItem.setValue(profile.getFullname());
        if (userId != null && token != null) {
            fullnameItem.setRequired(true);
            fullnameItem.addKeyPressHandler(new KeyPressHandler() {
                @Override
                public void onKeyPress(KeyPressEvent keyPressEvent) {
                    if ("enter".equalsIgnoreCase(keyPressEvent.getKeyName())) {
                        processSaveProfile(fullnameItem);
                    }
                }
            });
        } else {
            fullnameItem.disable();
        }

        form.setFields(new FormItem[] {fullnameItem});
        profileWnd.addItem(form);

        if (userId != null && token != null) {
            HLayout buttons = new HLayout();
            buttons.setLayoutAlign(Alignment.CENTER);
            buttons.setHeight(30);
            profileWnd.addItem(buttons);
            IButton saveBtn = new IButton("Save");
            buttons.addMember(saveBtn);
            saveBtn.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    processSaveProfile(fullnameItem);
                }
            });
        }
    }

    private void processSaveProfile(final TextItem fullnameItem) {
        mainView.getMenuView().writeStatus("");
        StringBuilder errorMsg = new StringBuilder("The following error(s) occured:<br/>");
        boolean isError = false;
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Form submitted with username[" + fullnameItem.getEnteredValue().trim() + "]");
        }
        if (fullnameItem.getEnteredValue().trim() == "") {
            isError = true;
            errorMsg.append("- Fullname field is empty<br/>");
        }

        if (isError) {
            profileMsg.setContents(errorMsg.toString());
        } else {
            profile.setFullname(fullnameItem.getEnteredValue().trim());
            mainView.getBackendService().saveProfile(userId, token, profile, new AsyncCallback<StatusResult>() {
                @Override
                public void onFailure(Throwable throwable) {
                    LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                    profileMsg.setContents("An unexpected error has occured.");
                }

                @Override
                public void onSuccess(final StatusResult result) {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Received statusResult:" + result.toString());
                    }
                    profileMsg.setContents(result.getStatus().getDescription());
                }
            });
        }
    }
}
