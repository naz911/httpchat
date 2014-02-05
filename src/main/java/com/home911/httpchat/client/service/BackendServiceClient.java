package com.home911.httpchat.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.home911.httpchat.client.model.*;
import com.home911.httpchat.shared.model.ContactFilterType;
import com.home911.httpchat.shared.model.Message;
import com.home911.httpchat.shared.model.Profile;

public interface BackendServiceClient {
    public void register(String username, String password, String email, AsyncCallback<StatusResult> callback);
    public void login(String username, String password, AsyncCallback<LoginResult> callback);
    public void logout(Long userId, String token, AsyncCallback<StatusResult> callback);
    public void search(Long userId, String token, String filterValue, ContactFilterType filterType, AsyncCallback<ContactsResult> callback);
    public void addContact(Long userId, String token, Long contactId, AsyncCallback<StatusResult> callback);
    public void saveProfile(Long userId, String token, Profile profile, AsyncCallback<StatusResult> callback);
    public void getProfile(Long userId, String token, Long id, AsyncCallback<ProfileResult> callback);
    public void poll(Long userId, String token, AsyncCallback<PollResult> callback);
    public void acceptInvite(Long userId, String token, Long id, AsyncCallback<StatusResult> callback);
    public void denyInvite(Long userId, String token, Long id, AsyncCallback<StatusResult> callback);
    public void removeContact(Long userId, String token, Long id, AsyncCallback<StatusResult> callback);
    public void send(Long userId, String token, Message message, AsyncCallback<StatusResult> callback);
}
