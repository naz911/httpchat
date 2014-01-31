package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.model.ProfileResult;
import com.home911.httpchat.client.model.StatusResult;
import com.home911.httpchat.shared.model.Contact;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.events.ErrorEvent;
import com.smartgwt.client.data.events.HandleErrorHandler;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RowContextClickEvent;
import com.smartgwt.client.widgets.grid.events.RowContextClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContactListView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(ContactListView.class.getName());

    private final Window contactWnd;
    private final ListGrid contactsGrid;
    private final ContactDataSource contactDs;
    private final MainView mainView;

    private final Long userId;
    private final String token;

    private static class ContactDataSource extends DataSource {
        private ContactDataSource(String id) {
            setID(id);
            DataSourceTextField pkDsField = new DataSourceTextField("id");
            pkDsField.setHidden(true);
            pkDsField.setPrimaryKey(true);

            DataSourceTextField nameDsfield = new DataSourceTextField("name", "Name");
            nameDsfield.setRequired(true);

            DataSourceTextField presenceDsField = new DataSourceTextField("presence", "Presence");
            presenceDsField.setRequired(true);

            setFields(pkDsField, nameDsfield, presenceDsField);
            setClientOnly(true);
            addHandleErrorHandler(new HandleErrorHandler() {
                @Override
                public void onHandleError(ErrorEvent errorEvent) {
                    LOGGER.log(Level.SEVERE, "An error has occured:" + errorEvent.getAssociatedType());
                }
            });
        }
    }

    public ContactListView(MainView mainView, Long userId, String token) {
        this.mainView = mainView;
        this.userId = userId;
        this.token = token;
        contactWnd = new Window();
        contactWnd.setTitle("HttpChat Contacts");
        contactWnd.setAutoSize(true);
        contactWnd.setAnimateMinimize(true);
        contactWnd.setCanDragResize(false);
        contactWnd.setCanDragReposition(true);
        contactWnd.setShowCloseButton(false);
        initWidget(contactWnd);

        contactDs = new ContactDataSource("contactsDS");

        contactsGrid = new ListGrid();
        contactsGrid.setWidth(310);
        contactsGrid.setHeight(250);
        contactsGrid.setAlternateRecordStyles(true);
        contactsGrid.setShowAllRecords(true);
        contactsGrid.setDataSource(contactDs);
        contactsGrid.setAutoFetchData(true);

        contactsGrid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
            @Override
            public void onRecordDoubleClick(RecordDoubleClickEvent event) {
                LOGGER.log(Level.INFO, "Doubled clicked will open conversation for contact["
                        + event.getRecord().getAttributeAsLong("id") + "]");
            }
        });

        contactsGrid.addRowContextClickHandler(new RowContextClickHandler() {
            public void onRowContextClick(RowContextClickEvent event) {
                Menu contactPopup = createContactPopup(event.getRecord().getAttributeAsLong("id"));
                // Show the popup
                contactPopup.showContextMenu();
                event.cancel();
            }
        });

        ListGridField idField = new ListGridField("id", "Id", 0);
        ListGridField nameField = new ListGridField("name", "Name", 200);
        ListGridField presenceField = new ListGridField("presence", "Presence", 100);

        contactsGrid.setFields(idField, nameField, presenceField);
        contactsGrid.setCanResizeFields(true);
        contactsGrid.hideFields(idField);
        contactWnd.addItem(contactsGrid);
    }

    public void hide() {
        contactWnd.destroy();
    }

    public void populateContactList(List<Contact> contacts) {
        LOGGER.log(Level.INFO, "Introducing new DS pattern...  !!!");
        if (contacts != null) {
            int idx = 0;
            ListGridRecord[] records = new ListGridRecord[contacts.size()];
            for (Contact contact : contacts) {
                ListGridRecord record = new ListGridRecord();
                record.setAttribute("id", String.valueOf(contact.getId()));
                record.setAttribute("name", contact.getName());
                record.setAttribute("presence", contact.getPresence().name());
                LOGGER.log(Level.INFO, "Adding record:" + record.toString());
                records[idx++] = record;
            }
            contactDs.setTestData(records);
        }
    }

    public void addContactToList(Contact contact) {
        if (contact != null) {
            ListGridRecord contactRec = new ListGridRecord();
            contactRec.setAttribute("id", String.valueOf(contact.getId()));
            contactRec.setAttribute("name", contact.getName());
            contactRec.setAttribute("presence", contact.getPresence().name());
            contactDs.addData(contactRec);
        }
    }

    public void removeContactFromList(Long id) {
        ListGridRecord contactRec = new ListGridRecord();
        contactRec.setAttribute("id", String.valueOf(id));
        contactDs.removeData(contactRec);
    }

    public void updateContactInList(Contact contact) {
        if (contact != null) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Updating contact:" + contact.toString());
            }
            ListGridRecord contactRec = new ListGridRecord();
            contactRec.setAttribute("id", String.valueOf(contact.getId()));
            contactRec.setAttribute("name", contact.getName());
            contactRec.setAttribute("presence", contact.getPresence().name());
            contactDs.updateData(contactRec);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Contact updated..");
            }
            contactWnd.flash();
        }
    }

    private Menu createContactPopup(final Long id) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Creating contact popup menu for id[" + id + "]");
        }
        final Menu menu = new Menu();
        menu.setShowShadow(true);
        menu.setShadowDepth(10);

        MenuItem profileItem = new MenuItem("Profile");
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem messageItem = new MenuItem("Message");

        menu.setItems(profileItem, removeItem, messageItem);

        profileItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent menuItemClickEvent) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Profile clicked...");
                }
                mainView.getBackendService().getProfile(userId, token, id,
                        new AsyncCallback<ProfileResult>() {

                            @Override
                            public void onFailure(Throwable throwable) {
                                LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                                mainView.getMenuView().writeStatus("Unable to get contact profile.");
                            }

                            @Override
                            public void onSuccess(ProfileResult profileResult) {
                                if (LOGGER.isLoggable(Level.INFO)) {
                                    LOGGER.log(Level.INFO, "Received result:" + profileResult.toString());
                                }
                                if (profileResult.getStatus().getCode() == 200) {
                                    ProfileView profileView = new ProfileView(mainView, profileResult.getProfile());
                                    profileView.display();
                                } else {
                                    mainView.getMenuView().writeStatus(profileResult.getStatus().getDescription());
                                }
                            }
                        });
            }
        });
        removeItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent menuItemClickEvent) {
                SC.confirm("Do you really want to delete this contact?", new BooleanCallback() {
                    public void execute(Boolean value) {
                        if (value != null && value) {
                            mainView.getBackendService().removeContact(userId, token, id,
                                    new AsyncCallback<StatusResult>() {

                                        @Override
                                        public void onFailure(Throwable throwable) {
                                            LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                                            mainView.getMenuView().writeStatus("Unable to remove contact.");
                                        }

                                        @Override
                                        public void onSuccess(StatusResult result) {
                                            if (LOGGER.isLoggable(Level.INFO)) {
                                                LOGGER.log(Level.INFO, "Received result:" + result.toString());
                                            }
                                            mainView.getMenuView().writeStatus(result.getStatus().getDescription());
                                            if (result.getStatus().getCode() == 200) {
                                                removeContactFromList(id);
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });
        messageItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent menuItemClickEvent) {
                LOGGER.log(Level.INFO, "Conversation clicked for contact[" + id + "]");
            }
        });

        return menu;
    }
}
