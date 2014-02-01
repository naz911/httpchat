package com.home911.httpchat.client.utils;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.home911.httpchat.shared.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ParserUtil {
    private static final Logger LOGGER = Logger.getLogger(ParserUtil.class.getName());

    private ParserUtil() {
    }

    public static List<Message> parseMessages(JSONObject response) {
        LOGGER.log(Level.INFO, "Parsing Messages...");
        JSONValue jsonMessagesValue = response.get("messages");
        if (jsonMessagesValue != null) {
            JSONArray jsonMessages = jsonMessagesValue.isArray();
            if (jsonMessages != null) {
                LOGGER.log(Level.INFO, "There is a Message obj.");
                List<Message> messages = new ArrayList<Message>(jsonMessages.size());
                for (int i=0;i<jsonMessages.size();i++) {
                    JSONObject jsonMessage = jsonMessages.get(i).isObject();
                    messages.add(parseMessage(jsonMessage));
                }
                return messages;
            }
        }
        LOGGER.log(Level.INFO, "No Message obj.");
        return null;
    }

    public static List<Alert> parseAlerts(JSONObject response) {
        LOGGER.log(Level.INFO, "Parsing Alerts...");
        JSONValue jsonAlertsValue = response.get("alerts");
        if (jsonAlertsValue != null) {
            JSONArray jsonAlerts = jsonAlertsValue.isArray();
            if (jsonAlerts != null) {
                LOGGER.log(Level.INFO, "There is a Alert obj.");
                List<Alert> alerts = new ArrayList<Alert>(jsonAlerts.size());
                for (int i=0;i<jsonAlerts.size();i++) {
                    JSONObject jsonAlert = jsonAlerts.get(i).isObject();
                    alerts.add(parseAlert(jsonAlert));
                }
                return alerts;
            }
        }
        LOGGER.log(Level.INFO, "No Alert obj.");
        return null;
    }

    public static List<Contact> parseContacts(JSONObject response) {
        LOGGER.log(Level.INFO, "Parsing Contacts...");
        JSONValue jsonContactsValue = response.get("contacts");
        if (jsonContactsValue != null) {
            JSONArray jsonContacts = jsonContactsValue.isArray();
            if (jsonContacts != null) {
                LOGGER.log(Level.INFO, "There is a Contacts obj.");
                List<Contact> contacts = new ArrayList<Contact>(jsonContacts.size());
                for (int i=0;i<jsonContacts.size();i++) {
                    JSONObject jsonContact = jsonContacts.get(i).isObject();
                    contacts.add(parseContact(jsonContact));
                }
                return contacts;
            }
        }
        LOGGER.log(Level.INFO, "No Contacts obj.");
        return null;
    }

    public static Message parseMessage(JSONObject jsonMessage) {
        LOGGER.log(Level.INFO, "One message found..." + jsonMessage);
        Message message = new Message();
        message.setText(JsonUtil.getStringValue(jsonMessage.get("text")));
        message.setFrom(JsonUtil.getLongValue(jsonMessage.get("from")));
        LOGGER.log(Level.INFO, "Returning message:" + message.toString());
        return message;
    }

    public static Alert parseAlert(JSONObject jsonAlert) {
        LOGGER.log(Level.INFO, "One alert found..." + jsonAlert);
        Alert alert = new Alert();
        alert.setId(JsonUtil.getLongValue(jsonAlert.get("id")));
        alert.setType(NotificationType.valueOf(JsonUtil.getStringValue(jsonAlert.get("type"))));
        alert.setReferer(JsonUtil.getLongValue(jsonAlert.get("referer")));
        if (NotificationType.CONTACT_INVITE == alert.getType()) {
            alert.setData(parseContact(jsonAlert.get("data").isObject()));
        } else if (NotificationType.PRESENCE == alert.getType()) {
            alert.setData(parseContact(jsonAlert.get("data").isObject()));
        }
        LOGGER.log(Level.INFO, "Returning alert:" + alert.toString());
        return alert;
    }

    public static Contact parseContact(JSONObject jsonContact) {
        LOGGER.log(Level.INFO, "One contact found..." + jsonContact);
        Contact contact = new Contact();
        contact.setId(JsonUtil.getLongValue(jsonContact.get("id")));
        contact.setName(JsonUtil.getStringValue(jsonContact.get("name")));
        contact.setPresence(Presence.valueOf(JsonUtil.getStringValue(jsonContact.get("presence"))));
        LOGGER.log(Level.INFO, "Returning contact:" + contact);
        return contact;
    }

    public static Status parseStatus(JSONObject response) {
        LOGGER.log(Level.INFO, "Parsing Status...");
        JSONObject jsonStatus = response.get("status").isObject();
        if (jsonStatus != null) {
            LOGGER.log(Level.INFO, "There is a Status obj.");
            Status status = new Status();
            status.setCode(JsonUtil.getIntValue(jsonStatus.get("code")));
            status.setDescription(JsonUtil.getStringValue(jsonStatus.get("description")));
            LOGGER.log(Level.INFO, "Status created..." + status.toString());
            return status;
        } else {
            LOGGER.log(Level.INFO, "No Status obj.");
            return null;
        }
    }

    public static Profile parseProfile(JSONObject response) {
        LOGGER.log(Level.INFO, "Parsing Profile...");
        JSONObject jsonProfile = response.get("profile").isObject();
        if (jsonProfile != null) {
            LOGGER.log(Level.INFO, "There is a Profile obj.");
            Profile profile = new Profile();
            profile.setId(JsonUtil.getLongValue(jsonProfile.get("id")));
            profile.setFullname(JsonUtil.getStringValue(jsonProfile.get("fullname")));
            LOGGER.log(Level.INFO, "Returning profile:" + profile);
            return profile;
        }
        LOGGER.log(Level.INFO, "No Profile obj.");
        return null;
    }

    public static Push parsePush(JSONObject response) {
        Push push = new Push();
        JSONValue jsonAlertValue = response.get("alert");
        JSONValue jsonMessageValue = response.get("message");

        if (jsonAlertValue != null) {
            push.setAlert(parseAlert(jsonAlertValue.isObject()));
        }
        if (jsonMessageValue != null) {
            push.setMessage(parseMessage(jsonMessageValue.isObject()));
        }
        LOGGER.log(Level.INFO, "Returning push:" + push.toString());
        return push;
    }
}
