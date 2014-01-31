package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.model.StatusResult;
import com.home911.httpchat.shared.model.Alert;
import com.home911.httpchat.shared.model.Contact;
import com.home911.httpchat.shared.model.Presence;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.events.ErrorEvent;
import com.smartgwt.client.data.events.HandleErrorHandler;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RowContextClickEvent;
import com.smartgwt.client.widgets.grid.events.RowContextClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(AlertView.class.getName());

    private final MainView mainView;
    private final Window alertWnd;
    private final ListGrid alertsGrid;
    private final AlertDataSource alertsDs;
    private final Long userId;
    private final String token;

    private static class AlertDataSource extends DataSource {
        private AlertDataSource(String id) {
            setID(id);
            DataSourceTextField pkDsField = new DataSourceTextField("id");
            pkDsField.setHidden(true);
            pkDsField.setPrimaryKey(true);

            DataSourceTextField typeDsfield = new DataSourceTextField("type", "Type");
            typeDsfield.setRequired(true);

            DataSourceTextField fromDsField = new DataSourceTextField("from", "From");
            fromDsField.setRequired(true);

            DataSourceTextField fromIdDsField = new DataSourceTextField("fromId", "FromId");
            fromIdDsField.setRequired(true);
            fromIdDsField.setHidden(true);
            DataSourceTextField fromPresenceDsField = new DataSourceTextField("fromPresence", "FromPresence");
            fromPresenceDsField.setRequired(true);
            fromPresenceDsField.setHidden(true);

            setFields(pkDsField, typeDsfield, fromDsField, fromIdDsField, fromPresenceDsField);
            setClientOnly(true);
            addHandleErrorHandler(new HandleErrorHandler() {
                @Override
                public void onHandleError(ErrorEvent errorEvent) {
                    LOGGER.log(Level.SEVERE, "An error has occured:" + errorEvent.getAssociatedType());
                }
            });
        }
    }

    public AlertView(MainView mainView, Long userId, String token) {
        this.mainView = mainView;
        this.userId = userId;
        this.token = token;
        alertWnd = new Window();
        alertWnd.setTitle("HttpChat Alerts");
        alertWnd.centerInPage();
        alertWnd.setTop(0);
        alertWnd.setAnimateMinimize(true);
        alertWnd.setAutoSize(true);
        alertWnd.setCanDragResize(false);
        alertWnd.setShowCloseButton(false);
        alertWnd.setShowMaximizeButton(false);
        alertWnd.setShowMinimizeButton(true);
        initWidget(alertWnd);

        alertWnd.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
                hide();
            }
        });

        alertsDs = new AlertDataSource("alertsDS");

        alertsGrid = new ListGrid();
        alertsGrid.setWidth(310);
        alertsGrid.setHeight(250);
        alertsGrid.setAlternateRecordStyles(true);
        alertsGrid.setShowAllRecords(true);
        alertsGrid.setDataSource(alertsDs);
        alertsGrid.setAutoFetchData(true);

        alertsGrid.addRecordClickHandler(new RecordClickHandler() {
            public void onRecordClick(RecordClickEvent event) {
                Menu contactPopup = createAlertPopup(event.getRecord());
                // Show the popup
                contactPopup.showContextMenu();
            }
        });

        alertsGrid.addRowContextClickHandler(new RowContextClickHandler() {
            public void onRowContextClick(RowContextClickEvent event) {
                Menu contactPopup = createAlertPopup(event.getRecord());
                // Show the popup
                contactPopup.showContextMenu();
                event.cancel();
            }
        });

        ListGridField idField = new ListGridField("id", "Id", 0);
        ListGridField typeField = new ListGridField("type", "Type", 200);
        ListGridField fromField = new ListGridField("from", "From", 100);
        ListGridField fromIdField = new ListGridField("fromId", "FromId", 0);
        ListGridField fromPresenceField = new ListGridField("fromPresence", "FromPresence", 0);

        alertsGrid.setFields(idField, typeField, fromField, fromIdField);
        alertsGrid.setCanResizeFields(true);
        alertsGrid.hideFields(idField, fromIdField, fromPresenceField);
        alertWnd.addItem(alertsGrid);
    }

    public void display() {
        alertWnd.show();
    }

    public void hide() {
        alertWnd.destroy();
    }

    public void addAlert(Alert alert) {
        if (alert != null) {
            ListGridRecord record = new ListGridRecord();
            record.setAttribute("id", alert.getId());
            record.setAttribute("type", alert.getType().name());
            switch (alert.getType()) {
                case CONTACT_INVITE:
                    Contact contact = (Contact) alert.getData();
                    record.setAttribute("from", contact.getName());
                    record.setAttribute("fromId", contact.getId());
                    record.setAttribute("fromPresence", contact.getPresence().name());
                    break;
            }
            int pos = alertsGrid.getRecordIndex(record);
            if (pos >= 0) {
                alertsDs.updateData(record);
            } else {
                alertsDs.addData(record);
                alertWnd.flash();
            }
        }
    }

    private Menu createAlertPopup(final ListGridRecord contactRec) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Creating alert popup menu for id[" + contactRec + "]");
        }
        final Menu menu = new Menu();
        menu.setShowShadow(true);
        menu.setShadowDepth(10);

        MenuItem acceptItem = new MenuItem("Accept");
        MenuItem denyItem = new MenuItem("Deny");

        menu.setItems(acceptItem, denyItem);

        acceptItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent menuItemClickEvent) {
                SC.confirm("Do you really want to accept this invite?", new BooleanCallback() {
                    Contact contact = new Contact(contactRec.getAttributeAsLong("fromId"),
                            contactRec.getAttribute("from"), Presence.valueOf(contactRec.getAttribute("fromPresence")));
                    public void execute(Boolean value) {
                        if (value != null && value) {
                            mainView.getBackendService().acceptInvite(userId, token, contactRec.getAttributeAsLong("id"),
                                    new AsyncCallback<StatusResult>() {
                                        @Override
                                        public void onFailure(Throwable throwable) {
                                            LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                                            mainView.getMenuView().writeStatus("An unexpected error has occured.");
                                        }

                                        @Override
                                        public void onSuccess(StatusResult result) {
                                            if (LOGGER.isLoggable(Level.INFO)) {
                                                LOGGER.log(Level.INFO, "Received statusResult:" + result.toString());
                                            }
                                            mainView.getMenuView().writeStatus(result.getStatus().getDescription());
                                            if (result.getStatus().getCode() == 200) {
                                                mainView.getContactListView().addContactToList(contact);
                                                alertsDs.removeData(contactRec);
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });

        denyItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent menuItemClickEvent) {
                SC.confirm("Do you really want to deny this invite?", new BooleanCallback() {
                    public void execute(Boolean value) {
                        if (value != null && value) {
                            mainView.getBackendService().denyInvite(userId, token, contactRec.getAttributeAsLong("id"),
                                    new AsyncCallback<StatusResult>() {
                                        @Override
                                        public void onFailure(Throwable throwable) {
                                            LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                                            mainView.getMenuView().writeStatus("An unexpected error has occured.");
                                        }

                                        @Override
                                        public void onSuccess(StatusResult result) {
                                            if (LOGGER.isLoggable(Level.INFO)) {
                                                LOGGER.log(Level.INFO, "Received statusResult:" + result.toString());
                                            }
                                            mainView.getMenuView().writeStatus(result.getStatus().getDescription());
                                            if (result.getStatus().getCode() == 200) {
                                                alertsDs.removeData(contactRec);
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });

        return menu;
    }
}
