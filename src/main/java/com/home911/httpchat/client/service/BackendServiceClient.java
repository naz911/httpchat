package com.home911.httpchat.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.home911.httpchat.client.model.*;
import com.home911.httpchat.shared.model.ContactFilterType;
import com.home911.httpchat.shared.model.Message;
import com.home911.httpchat.shared.model.Profile;

public interface BackendServiceClient {
    public void register(String username, String password, String email, AsyncCallback<StatusResult> callback);
    public void login(String username, String password, Boolean usePush, AsyncCallback<LoginResult> callback);
    public void logout(String token, AsyncCallback<StatusResult> callback);
    public void search(String token, String filterValue, ContactFilterType filterType, AsyncCallback<ContactsResult> callback);
    public void addContact(String token, Long contactId, AsyncCallback<StatusResult> callback);
    public void saveProfile(String token, Profile profile, AsyncCallback<StatusResult> callback);
    public void getProfile(String token, Long id, AsyncCallback<ProfileResult> callback);
    public void poll(String token, AsyncCallback<PollResult> callback);
    public void acceptInvite(String token, Long id, AsyncCallback<StatusResult> callback);
    public void denyInvite(String token, Long id, AsyncCallback<StatusResult> callback);
    public void removeContact(String token, Long id, AsyncCallback<StatusResult> callback);
    public void send(String token, Message message, AsyncCallback<StatusResult> callback);
}
