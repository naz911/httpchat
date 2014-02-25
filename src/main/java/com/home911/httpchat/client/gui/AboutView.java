package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.ui.Composite;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AboutView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(AboutView.class.getName());
    private static final String CONTENT = "<p align=\"center\"><b>HttpChat</b><br/>Thanks for chatting with us!<br/>" +
            "Any comments/questions can be send to:<br/><a href=\"mailto:benoit.houle@gmail.com?Subject=HttpChat\">" +
            "Benoit Houle</a><br/><br/>Version <i>1.0</i></p>";

    private final Window aboutWnd;
    private final HTMLPane aboutPane;

    public AboutView() {
        LOGGER.log(Level.INFO, "Creating about window...");
        this.aboutPane = new HTMLPane();
        aboutPane.setWidth(300);
        aboutPane.setPadding(5);
        aboutPane.setContents(CONTENT);

        aboutWnd = new Window();
        aboutWnd.setTitle("HttpChat About");
        aboutWnd.centerInPage();
        aboutWnd.setTop(-100);
        aboutWnd.setLeft(aboutWnd.getLeft() - 100);
        aboutWnd.setWidth(320);
        aboutWnd.setHeight(180);
        aboutWnd.setCanDragResize(false);
        aboutWnd.setShowCloseButton(true);
        aboutWnd.setShowMaximizeButton(false);
        aboutWnd.setShowMinimizeButton(false);
        aboutWnd.setIsModal(true);
        aboutWnd.setShowModalMask(true);
        aboutWnd.addItem(aboutPane);
        aboutWnd.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
                aboutWnd.hide();
            }
        });
        aboutWnd.hide();
        initWidget(aboutWnd);
    }

    public void display() {
        LOGGER.log(Level.INFO, "Displaying about window...");
        aboutWnd.show();
    }

    public void hide() {
        aboutWnd.hide();
    }
}
