package com.home911.httpchat.client.gui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.home911.httpchat.client.model.ContactsResult;
import com.home911.httpchat.client.model.StatusResult;
import com.home911.httpchat.shared.model.Contact;
import com.home911.httpchat.shared.model.ContactFilterType;
import com.home911.httpchat.shared.model.Presence;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RowContextClickEvent;
import com.smartgwt.client.widgets.grid.events.RowContextClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchContactView extends Composite {
    private static final Logger LOGGER = Logger.getLogger(SearchContactView.class.getName());

    private final MainView mainView;
    private final Window searchWnd;
    private final ListGrid contactsGrid;
    private final String token;

    private Label searchMsg;

    public SearchContactView(MainView mainView, String token) {
        this.mainView = mainView;
        this.token = token;
        searchWnd = new Window();
        searchWnd.setTitle("HttpChat Search Contacts");
        searchWnd.centerInPage();
        searchWnd.setTop(-500);
        searchWnd.setLeft(searchWnd.getLeft() - 100);
        searchWnd.setAutoSize(true);
        searchWnd.setCanDragResize(false);
        searchWnd.setShowCloseButton(true);
        searchWnd.setShowMaximizeButton(false);
        searchWnd.setShowMinimizeButton(false);
        searchWnd.setIsModal(true);
        searchWnd.setShowModalMask(true);

        searchWnd.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
                hide();
            }
        });

        LOGGER.log(Level.INFO, "Creating...");
        contactsGrid = new ListGrid();
        createSearchWindow();
        searchWnd.hide();
        initWidget(searchWnd);
    }

    public void display() {
        searchWnd.show();
    }

    public void hide() {
        searchWnd.hide();
    }

    private void createSearchWindow() {
        Label details = new Label("Please enter search criteria.");
        details.setHeight(30);
        details.setPadding(10);
        details.setLayoutAlign(Alignment.CENTER);
        details.setLayoutAlign(VerticalAlignment.CENTER);
        details.setWrap(false);
        details.setAlign(Alignment.CENTER);
        searchWnd.addItem(details);

        searchMsg = new Label();
        searchMsg.setAutoFit(true);
        searchMsg.setHeight(30);
        searchMsg.setPadding(10);
        searchMsg.setLayoutAlign(Alignment.CENTER);
        searchMsg.setLayoutAlign(VerticalAlignment.CENTER);
        searchMsg.setWrap(false);
        searchMsg.setAlign(Alignment.CENTER);
        searchWnd.addItem(searchMsg);

        final DynamicForm form = new DynamicForm();
        form.setWidth(300);
        form.setAutoFocus(true);
        form.setLayoutAlign(Alignment.CENTER);
        form.setLayoutAlign(VerticalAlignment.CENTER);

        final TextItem filterValueItem = new TextItem();
        filterValueItem.setTitle("Filter Value");
        filterValueItem.setRequired(true);

        final ComboBoxItem filterTypeLst = new ComboBoxItem();
        filterTypeLst.setTitle("Filter type");
        filterTypeLst.setType("comboBox");
        filterTypeLst.setValueMap("USERNAME", "FULLNAME", "EMAIL");

        form.setFields(new FormItem[] {filterValueItem, filterTypeLst});
        searchWnd.addItem(form);

        HLayout buttons = new HLayout();
        buttons.setLayoutAlign(Alignment.CENTER);
        buttons.setHeight(30);
        searchWnd.addItem(buttons);

        contactsGrid.setWidth(310);
        contactsGrid.setHeight(250);
        contactsGrid.setShowAllRecords(true);

        contactsGrid.addRecordClickHandler(new RecordClickHandler() {
            public void onRecordClick(RecordClickEvent event) {
                Menu contactPopup = createContactPopup(event.getRecord());
                // Show the popup
                contactPopup.showContextMenu();
            }
        });

        contactsGrid.addRowContextClickHandler(new RowContextClickHandler() {
            public void onRowContextClick(RowContextClickEvent event) {
                Menu contactPopup = createContactPopup(event.getRecord());
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
        searchWnd.addItem(contactsGrid);

        IButton searchBtn = new IButton("Search");
        searchBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                StringBuilder errorMsg = new StringBuilder("The following error(s) occured:<br/>");
                boolean isError = false;
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Form submitted with filterValue[" + filterValueItem.getEnteredValue().trim() + "]," +
                            " filterType[" + filterTypeLst.getEnteredValue() + "]");
                }
                if (filterValueItem.getEnteredValue().trim() == "") {
                    isError = true;
                    errorMsg.append("- Filter Value field is empty<br/>");
                }
                if (filterTypeLst.getEnteredValue().trim() == "") {
                    isError = true;
                    errorMsg.append("- Filter Type field is empty<br/>");
                }

                if (isError) {
                    searchMsg.setContents(errorMsg.toString());
                } else {
                    mainView.getBackendService().search(token, filterValueItem.getEnteredValue().trim(),
                            ContactFilterType.valueOf(filterTypeLst.getEnteredValue().trim()),
                            new AsyncCallback<ContactsResult>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    LOGGER.log(Level.SEVERE, "An unexpected error has occured.", throwable);
                                    searchMsg.setContents("An unexpected error has occured.");
                                }

                                @Override
                                public void onSuccess(final ContactsResult result) {
                                    if (LOGGER.isLoggable(Level.INFO)) {
                                        LOGGER.log(Level.INFO, "Received contactsResult:" + result.toString());
                                    }
                                    searchMsg.setContents(result.getStatus().getDescription());
                                    if (result.getStatus().getCode() == 200) {
                                        populateSearchResults(result.getContacts(), contactsGrid);
                                    }
                                }
                            });
                }
            }
        });

        buttons.addMember(searchBtn);
        IButton resetBtn = new IButton("Reset");
        resetBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.reset();
                contactsGrid.setData(new ListGridRecord[0]);
                searchMsg.setContents("");
            }
        });
        buttons.addMember(resetBtn);
    }

    private void populateSearchResults(List<Contact> contacts, ListGrid contactsGrid) {
        if (contacts != null) {
            ListGridRecord[] gridRecords = new ListGridRecord[contacts.size()];
            int count = 0;
            for (Contact contact : contacts) {
                gridRecords[count] = new ListGridRecord();
                gridRecords[count].setAttribute("id", contact.getId());
                gridRecords[count].setAttribute("name", contact.getName());
                gridRecords[count].setAttribute("presence", contact.getPresence().name());
                count++;
            }
            contactsGrid.setData(gridRecords);
        }
    }

    private Menu createContactPopup(final ListGridRecord contactRec) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Creating contact popup menu for id[" + contactRec + "]");
        }
        final Menu menu = new Menu();
        menu.setShowShadow(true);
        menu.setShadowDepth(10);

        MenuItem addItem = new MenuItem("Add");

        menu.setItems(addItem);

        addItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent menuItemClickEvent) {
                SC.confirm("Do you really want to add this contact?", new BooleanCallback() {
                    Contact contact = new Contact(contactRec.getAttributeAsLong("id"),
                            contactRec.getAttribute("name"), Presence.OFFLINE);
                    public void execute(Boolean value) {
                        if (value != null && value) {
                            mainView.getBackendService().addContact(token, contact.getId(),
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
                                                contactsGrid.removeData(contactRec);
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