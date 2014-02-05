package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.model.StatusResult;
import com.home911.httpchat.shared.model.Message;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
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

public class MessageView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(MessageView.class.getName());

    private final Window messageWnd;
    private final HTMLPane messagePane;
    private final MainView mainView;

    private final Long ownerId;
    private final Long userId;
    private final String token;

    public MessageView(MainView mainView, String title, Long ownerId, Long userId, String token) {
        this.mainView = mainView;
        this.ownerId = ownerId;
        this.userId = userId;
        this.token = token;
        this.messagePane = new HTMLPane();
        messagePane.setWidth(300);
        messagePane.setHeight(200);
        messagePane.setPadding(5);

        messageWnd = new Window();
        messageWnd.setTitle(title);
        messageWnd.setAutoSize(true);
        messageWnd.setTop(-500);
        messageWnd.setCanDragResize(false);
        messageWnd.setCanDragReposition(true);
        messageWnd.setShowCloseButton(true);
        messageWnd.setShowMinimizeButton(false);
        messageWnd.addItem(messagePane);
        createSendMessageWindow();

        messageWnd.addCloseClickHandler(new CloseClickHandler() {
            @Override
            public void onCloseClick(CloseClickEvent closeClickEvent) {
                messageWnd.hide();
                closeClickEvent.cancel();
            }
        });

        initWidget(messageWnd);
    }

    private void createSendMessageWindow() {
        HLayout layout = new HLayout();
        layout.setWidth(300);
        layout.setHeight(25);

        final DynamicForm form = new DynamicForm();
        form.setWidth(200);
        form.setHeight(20);
        form.setAutoFocus(true);
        form.setLayoutAlign(Alignment.CENTER);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);

        final TextItem textItem = new TextItem();
        textItem.setHeight(25);
        textItem.setWidth(200);
        textItem.setRequired(true);
        textItem.setShowTitle(false);
        textItem.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent keyPressEvent) {
                if ("enter".equalsIgnoreCase(keyPressEvent.getKeyName())) {
                    processSendMessage(textItem.getEnteredValue().trim());
                    form.reset();
                }
            }
        });

        form.setFields(new FormItem[] {textItem});
        layout.addMember(form);

        IButton sendBtn = new IButton("Send");
        sendBtn.setHeight(25);
        sendBtn.setWidth(100);
        sendBtn.setLayoutAlign(VerticalAlignment.CENTER);
        layout.addMember(sendBtn);

        sendBtn.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(ClickEvent event) {
                processSendMessage(textItem.getEnteredValue().trim());
                form.reset();
            }
        });

        messageWnd.addItem(layout);
    }

    private void processSendMessage(String text) {
        if (text != "") {
            final Message message = new Message();
            message.setTo(ownerId);
            message.setText(text);
            mainView.getBackendService().send(userId, token, message,
                    new AsyncCallback<StatusResult>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                            mainView.getMenuView().writeStatus("An unexpected error has occured.");
                        }

                        @Override
                        public void onSuccess(final StatusResult result) {
                            if (LOGGER.isLoggable(Level.INFO)) {
                                LOGGER.log(Level.INFO, "Received statusResult:" + result.toString());
                            }
                            messageSent(message);
                        }
                    });
        }
    }

    public void display() {
        messageWnd.show();
    }

    public void hide() {
        messageWnd.destroy();
    }

    public void setTitle(String name) {
        this.messageWnd.setTitle(name);
    }

    public void messageReceive(Message message) {
        LOGGER.log(Level.INFO, "Message received:" + message.toString());
        if (message != null) {
            this.messagePane.setContents(this.messagePane.getContents() + "<p align=\"left\"><b>"
                    + message.getFrom().getName() + "</b><br />" + message.getText() + "</p>");
            this.messagePane.animateScroll(0, this.messagePane.getScrollBottom() + 100);
        }
    }

    public void messageSent(Message message) {
        LOGGER.log(Level.INFO, "Message sent:" + message.toString());
        if (message != null) {
            this.messagePane.setContents(this.messagePane.getContents() + "<p align=\"right\"><b>Me</b>" +
                    "<br />" + message.getText() + "</p>");
            this.messagePane.animateScroll(0, this.messagePane.getScrollBottom() + 100);
        }
    }
}