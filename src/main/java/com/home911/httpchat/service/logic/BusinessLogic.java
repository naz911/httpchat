package com.home911.httpchat.service.logic;

import com.home911.httpchat.servlet.event.RequestEvent;
import com.home911.httpchat.servlet.event.ResponseEvent;
import com.home911.httpchat.servlet.primitive.*;

public interface BusinessLogic {
    public ResponseEvent<LoginResponse> processLogin(RequestEvent<LoginRequest> requestEvent);
    public ResponseEvent<StatusResponse> processLogout(RequestEvent<LogoutRequest> requestEvent);
    public ResponseEvent<StatusResponse> processRegister(RequestEvent<RegisterRequest> requestEvent);
    public ResponseEvent<StatusResponse> processUpdateProfile(RequestEvent<UpdateProfileRequest> requestEvent);
    public ResponseEvent<ContactSearchResponse> processSearchContacts(RequestEvent<ContactSearchRequest> requestEvent);
    public ResponseEvent<StatusResponse> processContactInvite(RequestEvent<ContactInviteRequest> requestEvent);
    public ResponseEvent<StatusResponse> acceptContactInvite(RequestEvent<AcceptContactInviteRequest> requestEvent);
    public ResponseEvent<StatusResponse> denyContactInvite(RequestEvent<DenyContactInviteRequest> requestEvent);
    public ResponseEvent<StatusResponse> processPoll(RequestEvent<PollRequest> requestEvent);
    public ResponseEvent<GetProfileResponse> processGetProfile(RequestEvent<GetProfileRequest> requestEvent);
    public ResponseEvent<GetContactsResponse> processGetContacts(RequestEvent<GetContactsRequest> requestEvent);
    public ResponseEvent<StatusResponse> processSendMessage(RequestEvent<SendMessageRequest> requestEvent);
}
