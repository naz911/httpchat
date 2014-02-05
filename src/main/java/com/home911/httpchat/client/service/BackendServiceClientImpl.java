package com.home911.httpchat.client.service;

import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.home911.httpchat.client.gui.MainView;
import com.home911.httpchat.client.model.*;
import com.home911.httpchat.client.utils.Base64Coder;
import com.home911.httpchat.client.utils.ParserUtil;
import com.home911.httpchat.shared.model.ContactFilterType;
import com.home911.httpchat.shared.model.Message;
import com.home911.httpchat.shared.model.Profile;
import com.home911.httpchat.shared.model.Status;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BackendServiceClientImpl implements BackendServiceClient {
    private static final Logger LOGGER = Logger.getLogger(BackendServiceClientImpl.class.getName());
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE = "application/json";
    private static final String TOKEN_HEADER = "x-httpchat-token";
    private static final String USERID_HEADER = "x-httpchat-userid";

    private static final String REGISTER_REQ = "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}";
    private static final String LOGIN_REQ = "{\"username\":\"%s\",\"password\":\"%s\",\"usePush\":%b}";
    private static final String PROFILE_REQ = "{\"fullname\":\"%s\"}";
    private static final String MESSAGE_REQ = "{\"to\":%l,\"text\":\"%s\"}";

    private final MainView mainView;
    private final String baseUrl;

    public BackendServiceClientImpl(String baseUrl) {
        LOGGER.log(Level.INFO, "Received baseUrl[" + baseUrl + "]");
        this.mainView = new MainView(this);
        this.baseUrl = baseUrl;
    }

    private String getBaseUrl() {
        LOGGER.log(Level.INFO, "getBaseUrl[" + this.baseUrl + "]");
        return this.baseUrl;
    }

    public MainView getMainView() {
        return this.mainView;
    }

    @Override
    public void register(String username, String password, String email, final AsyncCallback<StatusResult> callback) {
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, getBaseUrl() + "rest/register");
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        try {
            reqBuilder.sendRequest(format(REGISTER_REQ, username, password, email), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    StatusResult result = new StatusResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void login(String username, String password, Boolean usePush, final AsyncCallback<LoginResult> callback) {
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, getBaseUrl() + "rest/login");
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        try {
            reqBuilder.sendRequest(format(format(LOGIN_REQ, username, password), usePush), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    LOGGER.log(Level.INFO, "Login response received:" + response.getText());
                    LoginResult result = new LoginResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        LOGGER.log(Level.INFO, "It is a login response success!");
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                            if (status.getCode() == 200) {
                                LOGGER.log(Level.INFO, "Continuing parsing...");
                                result.setProfile(ParserUtil.parseProfile(obj));
                                result.setContacts(ParserUtil.parseContacts(obj));
                                result.setAlerts(ParserUtil.parseAlerts(obj));
                                result.setToken(response.getHeader(TOKEN_HEADER));
                                result.setUserId(Long.valueOf(response.getHeader(USERID_HEADER)));
                                result.setChannelToken(parseChannelToken(obj));
                            }
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }
                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void logout(Long userId, String token, final AsyncCallback<StatusResult> callback) {
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, getBaseUrl() + "rest/secure/logout");
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    StatusResult result = new StatusResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void search(Long userId, String token, String filterValue,
                       ContactFilterType filterType, final AsyncCallback<ContactsResult> callback) {
        StringBuilder queryParams = new StringBuilder("?");
        queryParams.append("filterTypes=").append(filterType.name());
        queryParams.append("&filterValue=").append(filterValue);
        queryParams.append("&limit=").append(10);
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.GET, getBaseUrl() + "rest/secure/contacts/s" + queryParams.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    ContactsResult result = new ContactsResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                            if (status.getCode() == 200) {
                                LOGGER.log(Level.INFO, "Continuing parsing...");
                                result.setContacts(ParserUtil.parseContacts(obj));
                            }
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void addContact(Long userId, String token, Long contactId, final AsyncCallback<StatusResult> callback) {
        StringBuilder urlPath = new StringBuilder("rest/secure/contact/");
        urlPath.append(contactId).append("/invite");
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, getBaseUrl() + urlPath.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    StatusResult result = new StatusResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void saveProfile(Long userId, String token, Profile profile, final AsyncCallback<StatusResult> callback) {
        LOGGER.log(Level.INFO, "saveProfile called!");
        StringBuilder urlPath = new StringBuilder("rest/secure/profile");
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, getBaseUrl() + urlPath.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest(format(PROFILE_REQ, profile.getFullname()), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    LOGGER.log(Level.INFO, "saveProfile response received:" + response);
                    StatusResult result = new StatusResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        LOGGER.log(Level.INFO, "saveProfile response success...");
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        LOGGER.log(Level.INFO, "saveProfile response failure...");
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    public void getProfile(Long userId, String token, Long id, final AsyncCallback<ProfileResult> callback) {
        LOGGER.log(Level.INFO, "getProfile called!");
        StringBuilder urlPath = new StringBuilder("rest/secure/profile/");
        urlPath.append(id).append("?filterType=FULL");
        LOGGER.log(Level.INFO, "getProfile URL:" + getBaseUrl() + urlPath.toString());
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.GET, getBaseUrl() + urlPath.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    LOGGER.log(Level.INFO, "getProfile response received:" + response);
                    ProfileResult result = new ProfileResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        LOGGER.log(Level.INFO, "getProfile response success...");
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                            if (status.getCode() == 200) {
                                LOGGER.log(Level.INFO, "Continuing parsing...");
                                result.setProfile(ParserUtil.parseProfile(obj));
                            }
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        LOGGER.log(Level.INFO, "getProfile response failure...");
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    public void poll(Long userId, String token, final AsyncCallback<PollResult> callback) {
        LOGGER.log(Level.INFO, "poll called!");
        StringBuilder urlPath = new StringBuilder("rest/secure/alerts/");
        LOGGER.log(Level.INFO, "poll URL:" + getBaseUrl() + urlPath.toString());
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.GET, getBaseUrl() + urlPath.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    LOGGER.log(Level.INFO, "poll response received:" + response);
                    PollResult result = new PollResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        LOGGER.log(Level.INFO, "poll response success...");
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                            if (status.getCode() == 200) {
                                LOGGER.log(Level.INFO, "Continuing parsing...");
                                result.setAlerts(ParserUtil.parseAlerts(obj));
                                result.setMessages(ParserUtil.parseMessages(obj));
                            }
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        LOGGER.log(Level.INFO, "poll response failure...");
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void acceptInvite(Long userId, String token, Long id, final AsyncCallback<StatusResult> callback) {
        LOGGER.log(Level.INFO, "acceptInvite called!");
        StringBuilder urlPath = new StringBuilder("rest/secure/contact/invite/");
        urlPath.append(id).append("/accept");
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, getBaseUrl() + urlPath.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    LOGGER.log(Level.INFO, "acceptInvite response received:" + response);
                    StatusResult result = new StatusResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        LOGGER.log(Level.INFO, "acceptInvite response success...");
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        LOGGER.log(Level.INFO, "acceptInvite response failure...");
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void denyInvite(Long userId, String token, Long id, final AsyncCallback<StatusResult> callback) {
        LOGGER.log(Level.INFO, "denyInvite called!");
        StringBuilder urlPath = new StringBuilder("rest/secure/contact/invite/");
        urlPath.append(id).append("/deny");
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, getBaseUrl() + urlPath.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    LOGGER.log(Level.INFO, "denyInvite response received:" + response);
                    StatusResult result = new StatusResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        LOGGER.log(Level.INFO, "denyInvite response success...");
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        LOGGER.log(Level.INFO, "denyInvite response failure...");
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void removeContact(Long userId, String token, Long id, final AsyncCallback<StatusResult> callback) {
        LOGGER.log(Level.INFO, "removeContact called!");
        StringBuilder urlPath = new StringBuilder("rest/secure/contact/");
        urlPath.append(id);
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.DELETE, getBaseUrl() + urlPath.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    LOGGER.log(Level.INFO, "removeContact response received:" + response);
                    StatusResult result = new StatusResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        LOGGER.log(Level.INFO, "removeContact response success...");
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        LOGGER.log(Level.INFO, "removeContact response failure...");
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void send(Long userId, String token, Message message, final AsyncCallback<StatusResult> callback) {
        LOGGER.log(Level.INFO, "send called!");
        StringBuilder urlPath = new StringBuilder("rest/secure/message");
        RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, getBaseUrl() + urlPath.toString());
        reqBuilder.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        reqBuilder.setHeader("Authorization", getAuthorizationHeaderValue(userId, token));
        try {
            reqBuilder.sendRequest(format(format(MESSAGE_REQ, message.getTo()), message.getText()), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    LOGGER.log(Level.INFO, "send response received:" + response);
                    StatusResult result = new StatusResult();
                    Status status;
                    if (200 == response.getStatusCode()) {
                        LOGGER.log(Level.INFO, "send response success...");
                        JSONValue value = JSONParser.parseStrict(response.getText());
                        if (value != null) {
                            JSONObject obj = value.isObject();
                            status = ParserUtil.parseStatus(obj);
                        } else {
                            status = new Status(500, "Problem parsing the response!");
                        }
                    } else {
                        LOGGER.log(Level.INFO, "send response failure...");
                        status = new Status(response.getStatusCode(), response.getStatusText());
                    }

                    result.setStatus(status);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    private String format(final String format, final String... args) {
        String[] split = format.split("%s");
        final StringBuilder msg = new StringBuilder();
        for (int pos = 0; pos < split.length - 1; pos += 1) {
            msg.append(split[pos]);
            msg.append(args[pos]);
        }
        msg.append(split[split.length - 1]);
        return msg.toString();
    }

    private String format(final String format, final Long... args) {
        String[] split = format.split("%l");
        final StringBuilder msg = new StringBuilder();
        for (int pos = 0; pos < split.length - 1; pos += 1) {
            msg.append(split[pos]);
            msg.append(args[pos]);
        }
        msg.append(split[split.length - 1]);
        return msg.toString();
    }

    private String format(final String format, final Boolean... args) {
        String[] split = format.split("%b");
        final StringBuilder msg = new StringBuilder();
        for (int pos = 0; pos < split.length - 1; pos += 1) {
            msg.append(split[pos]);
            msg.append(args[pos]);
        }
        msg.append(split[split.length - 1]);
        return msg.toString();
    }


    private String parseChannelToken(JSONObject response) {
        LOGGER.log(Level.INFO, "Parsing channelToken...");
        JSONValue jsonChannelTokenValue = response.get("channelToken");
        if (jsonChannelTokenValue != null) {
            return jsonChannelTokenValue.isString().stringValue();
        }
        return null;
    }

    private String getAuthorizationHeaderValue(Long userId, String token) {
        StringBuilder authorization = new StringBuilder("Basic ");
        authorization.append(Base64Coder.encodeString(String.valueOf(userId) + ":" + token));
        return authorization.toString();
    }
}
