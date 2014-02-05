package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.ui.Composite;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;

import java.util.logging.Logger;

public class LoadingView extends Composite {
    private final Window loadingWnd;
    private Label loadingMsg;

    public LoadingView() {
        loadingMsg = new Label();
        loadingMsg.setHeight(30);
        loadingMsg.setPadding(10);
        loadingMsg.setWrap(false);
        loadingMsg.setAlign(Alignment.CENTER);
        loadingWnd = new Window();
        loadingWnd.setTitle("HttpChat Loading");
        loadingWnd.centerInPage();
        loadingWnd.setAutoSize(true);
        loadingWnd.setCanDragResize(false);
        loadingWnd.setShowCloseButton(false);
        loadingWnd.setShowMaximizeButton(false);
        loadingWnd.setShowMinimizeButton(false);
        loadingWnd.setIsModal(true);
        loadingWnd.setShowModalMask(true);
        createLoadingWindow();
        loadingWnd.hide();
        initWidget(loadingWnd);
    }

    public void display(String label) {
        loadingMsg.setContents("<b>" + label + "</b>");
        loadingWnd.show();
    }

    public void hide() {
        loadingWnd.hide();
    }

    private void createLoadingWindow() {
        loadingWnd.setAlign(Alignment.CENTER);
        loadingWnd.setAlign(VerticalAlignment.CENTER);
        loadingWnd.addItem(loadingMsg);
        Img image = new Img("loading.gif");
        image.setLayoutAlign(Alignment.CENTER);
        loadingWnd.addItem(image);
    }
}
