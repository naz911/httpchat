package com.home911.httpchat.server.service.logic;

import com.google.inject.Inject;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.TxnType;
import com.home911.httpchat.server.exception.*;
import com.home911.httpchat.server.model.*;
import com.home911.httpchat.server.model.Message;
import com.home911.httpchat.server.service.email.EmailService;
import com.home911.httpchat.server.service.message.MessageService;
import com.home911.httpchat.server.service.notification.NotificationPusher;
import com.home911.httpchat.server.service.notification.NotificationService;
import com.home911.httpchat.server.service.user.UserService;
import com.home911.httpchat.server.service.userinfo.UserInfoService;
import com.home911.httpchat.server.servlet.event.RequestEvent;
import com.home911.httpchat.server.servlet.event.ResponseEvent;
import com.home911.httpchat.server.servlet.primitive.*;
import com.home911.httpchat.server.transaction.Transaction;
import com.home911.httpchat.shared.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BusinessLogicImpl implements BusinessLogic {
    private static final Logger LOGGER = Logger.getLogger(BusinessLogicImpl.class.getCanonicalName());

    private static final String UTF8 = "UTF-8";

    private final UserService userService;
    private final UserInfoService userInfoService;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final EmailService emailService;
    private final NotificationPusher notificationPusher;


    @Inject
    public BusinessLogicImpl(UserService userService, UserInfoService userInfoService,
                             NotificationService notificationService, MessageService messageService,
                             EmailService emailService, NotificationPusher notificationPusher) {
        this.userService = userService;
        this.userInfoService = userInfoService;
        this.notificationService = notificationService;
        this.messageService = messageService;
        this.emailService = emailService;
        this.notificationPusher = notificationPusher;
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<LoginResponse> processLogin(RequestEvent<LoginRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing login request[" + requestEvent + "]");
        LoginRequest req = requestEvent.getRequest();
        User user = userService.getUser(req.getUsername(), req.getPassword());

        if (user != null) {
            if (!user.isActivated()) {
                throw new UserNotActivatedException();
            }
            // Successfull login
            String token = generateToken(user.getUsername());

            // update his presence
            user.setPresence(Presence.ONLINE);
            user.setToken(token);

            if (user.getUserInfo() == null) {
                UserInfo userInfo = new UserInfo();
                userInfo.setOwner(user);
                userInfoService.saveUserInfo(userInfo);
                user.setUserInfo(userInfo);
            }
            userService.saveUser(user);

            String channelToken = null;
            if (req.isUsePush()) {
                channelToken = notificationPusher.createChannel(String.valueOf(user.getId()));
            }

            Profile profile = new Profile();
            profile.setFullname(user.getUserInfo().getFullname());

            List<Contact> ownContacts = new ArrayList<Contact>(user.getUserInfo().getContacts().size());
            List<Notification> notifications = new ArrayList<Notification>();
            List<Notification> pushNotifications = new ArrayList<Notification>();

            for (Ref<User> contactRef : user.getUserInfo().getContacts()) {
                User contact = contactRef.get();
                String name = StringUtils.isEmpty(contact.getUserInfo().getFullname()) ? contact.getUsername() :
                        contact.getUserInfo().getFullname();
                ownContacts.add(new Contact(contact.getId(), name, contact.getPresence()));

                // generate presence notifications
                if (Presence.ONLINE == contact.getPresence()) {
                    Notification notif = new Notification();
                    notif.setOwner(contact);
                    notif.setType(NotificationType.PRESENCE);
                    notif.setReferer(user);
                    notif.setData(Presence.ONLINE.name());

                    if (contact.isAvailableForPush()) {
                        pushNotifications.add(notif);
                    } else {
                        notifications.add(notif);
                    }
                }
            }

            // Adding "pending" contacts as OFFLINE
            for (Ref<User> contactRef : user.getUserInfo().getPendingContacts()) {
                User contact = contactRef.get();
                String name = StringUtils.isEmpty(contact.getUserInfo().getFullname()) ? contact.getUsername() :
                        contact.getUserInfo().getFullname();
                ownContacts.add(new Contact(contact.getId(), name, Presence.OFFLINE));
            }

            // generate login response
            LoginResponse resp = new LoginResponse(HttpStatus.SC_OK, "Login successful", token,
                    user.getId(), profile, channelToken);
            resp.setContacts(ownContacts);

            // generate presence notifications
            if (!notifications.isEmpty()) {
                notificationService.addNotifications(notifications);
            }

            if (!pushNotifications.isEmpty()) {
                notificationPusher.push(pushNotifications);
            }

            getOwnNotification(user, resp, true);

            return new ResponseEvent<LoginResponse>(resp);

        } else {
            throw new InvalidCredentialsException();
        }
    }

    //@Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processLogout(RequestEvent<LogoutRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing logout request[" + requestEvent + "]");
        User user = userService.getUser(requestEvent.getUserId());

        if (user != null) {
            // update his presence
            user.setPresence(Presence.OFFLINE);
            user.setToken(null);
            user.setChannelConnected(false);
            userService.saveUser(user);

            List<Notification> notifications = new ArrayList<Notification>();
            List<Notification> pushNotifications = new ArrayList<Notification>();

            for (Ref<User> contactRef : user.getUserInfo().getContacts()) {
                User contact = contactRef.get();
                if (Presence.ONLINE == contact.getPresence()) {
                    Notification notif = new Notification();
                    notif.setOwner(contact);
                    notif.setType(NotificationType.PRESENCE);
                    notif.setReferer(user);
                    notif.setData(Presence.OFFLINE.name());

                    if (contact.isAvailableForPush()) {
                        pushNotifications.add(notif);
                    } else {
                        notifications.add(notif);
                    }
                }
            }
            if (!notifications.isEmpty()) {
                notificationService.addNotifications(notifications);
            }

            if (!pushNotifications.isEmpty()) {
                notificationPusher.push(pushNotifications);
            }

            // get own notifications
            List<Notification> ownNotifications = notificationService.getNotifications(user);
            if (ownNotifications != null && !ownNotifications.isEmpty()) {
                List<Notification> notificationsToRemove = new ArrayList<Notification>();
                for(Notification notif : ownNotifications) {
                    if (NotificationType.PRESENCE == notif.getType()) {
                        notificationsToRemove.add(notif);
                    }
                }
                if (!notificationsToRemove.isEmpty()) {
                    notificationService.removeNotifications(user, notificationsToRemove);
                }
            }
        }

        return new ResponseEvent<StatusResponse>(new StatusResponse(HttpStatus.SC_OK, "Logout successful"));
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<RegisterResponse> processRegister(RequestEvent<RegisterRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing register request[" + requestEvent + "]");
        RegisterRequest req = requestEvent.getRequest();
        if (userService.exists(req.getUsername())) {
            throw new UserAlreadyExistException();
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setEmail(req.getEmail());
        //user.setActivated(true);
        userService.saveUser(user);


        StringBuilder confirmUrl = new StringBuilder(req.getConfirmUrl());
        confirmUrl.append(generateRegisterCode(user));
        emailService.sendRegistrationEmail(user, confirmUrl.toString());

        return new ResponseEvent<RegisterResponse>(
                new RegisterResponse(HttpStatus.SC_OK, "Register successful", confirmUrl.toString()));
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processConfirmRegister(RequestEvent<ConfirmRegisterRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing confirm register request[" + requestEvent + "]");
        ConfirmRegisterRequest req = requestEvent.getRequest();
        Long userId = parseRegisterCode(req.getCode());

        User user = userService.getUser(userId);
        if (user == null) {
            throw new UnknownUserException();
        }

        user.setActivated(true);
        userService.saveUser(user);

        return new ResponseEvent<StatusResponse>(new StatusResponse(HttpStatus.SC_OK, "Registration Confirmation successful"));
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processUpdateProfile(RequestEvent<UpdateProfileRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing updateProfile request[" + requestEvent + "]");
        UpdateProfileRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());

        if (user != null) {
            user.getUserInfo().setFullname(req.getProfile().getFullname());
            userInfoService.saveUserInfo(user.getUserInfo());

            StatusResponse resp = new StatusResponse(HttpStatus.SC_OK, "Update Profile successful");
            //getOwnNotification(user, resp);

            return new ResponseEvent<StatusResponse>(resp);
        } else {
            throw new InvalidUserSessionException();
        }
    }

    public ResponseEvent<ContactSearchResponse> processSearchContacts(RequestEvent<ContactSearchRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing searchContacts request[" + requestEvent + "]");
        ContactSearchRequest req = requestEvent.getRequest();

        List<User> users = userService.getUsers(getContactSearchFilter(req.getFilterTypes()), req.getFilterValue(),
                req.getOffset(), req.getLimit());

        final ContactSearchResponse resp;
        if (users == null || users.isEmpty()) {
            // no content
            resp = new ContactSearchResponse(HttpStatus.SC_NO_CONTENT, "No Contact Found");
        } else {
            resp = new ContactSearchResponse(HttpStatus.SC_OK, "Search Contact succesful");
            for (User user : users) {
                String name = user.getUserInfo() != null && StringUtils.isEmpty(user.getUserInfo().getFullname()) ?
                        user.getUsername() : user.getUserInfo().getFullname();
                resp.addContact(new Contact(user.getId(), name, user.getPresence()));
            }
        }

        //User user = userService.getUser(requestEvent.getUserId());
        //getOwnNotification(user, resp);

        return new ResponseEvent<ContactSearchResponse>(resp);
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processContactInvite(RequestEvent<ContactInviteRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing contactInvite request[" + requestEvent + "]");
        ContactInviteRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());

        if (user != null) {
            UserInfo userInfo = user.getUserInfo();
            User contact = userService.getUser(req.getId());

            if (userInfo.isContact(contact)) {
                // already in contact list..
                throw new ContactAlreadyExistException();
            }

            if (userInfo.isPendingContact(contact)) {
                throw new ContactInviteAlreadyExistException();
            }

            List<Notification> notifications = new ArrayList<Notification>();
            Notification pushNotif = null;

            if (contact.getUserInfo().isPendingContact(user)) {
                userInfo.addContact(contact);
                contact.getUserInfo().addContact(user);
                userInfoService.saveUserInfo(contact.getUserInfo());
                if (Presence.ONLINE == contact.getPresence()) {
                    Notification newNotif = new Notification();
                    newNotif.setOwner(contact);
                    newNotif.setType(NotificationType.PRESENCE);
                    newNotif.setReferer(user);
                    newNotif.setData(Presence.ONLINE.name());

                    if (!contact.isAvailableForPush()) {
                        pushNotif = newNotif;
                    } else {
                        notifications.add(newNotif);
                    }
                }
            } else {
                userInfo.addPendingContact(contact);
                Notification notif = new Notification();
                notif.setOwner(contact);
                notif.setType(NotificationType.CONTACT_INVITE);
                notif.setReferer(user);
                notifications.add(notif);
                if (contact.isAvailableForPush()) {
                    pushNotif = notif;
                }
            }

            if (!notifications.isEmpty()) {
                notificationService.addNotifications(notifications);
            }

            if (pushNotif != null) {
                notificationPusher.push(pushNotif);
            }

            userInfoService.saveUserInfo(userInfo);

            StatusResponse resp = new StatusResponse(HttpStatus.SC_OK, "Contact Invite successful");
            //getOwnNotification(user, resp);

            return new ResponseEvent<StatusResponse>(resp);
        } else {
            throw new InvalidUserSessionException();
        }
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> acceptContactInvite(RequestEvent<AcceptContactInviteRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing acceptContactInvite request[" + requestEvent + "]");
        AcceptContactInviteRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());
        Notification notif = notificationService.getNotification(user, req.getId());
        User referer = notif.getReferer();

        if (user != null) {
            user.getUserInfo().addContact(referer);
            // update user's userInfo
            userInfoService.saveUserInfo(user.getUserInfo());

            referer.getUserInfo().addContact(user);
            // update referer's userInfo
            userInfoService.saveUserInfo(referer.getUserInfo());

            // process old notif to remove
            List<Notification> notifications = new ArrayList<Notification>();
            notifications.add(notif);
            notificationService.removeNotifications(user, notifications);

            notifications.clear();

            if (Presence.ONLINE == referer.getPresence()) {
                Notification newNotif = new Notification();
                newNotif.setOwner(referer);
                newNotif.setType(NotificationType.PRESENCE);
                newNotif.setReferer(user);
                newNotif.setData(Presence.ONLINE.name());
                if (referer.isAvailableForPush()) {
                    notificationPusher.push(newNotif);
                } else {
                    notifications.add(newNotif);
                }
            }

            if (!notifications.isEmpty()) {
                notificationService.addNotifications(notifications);
            }

            StatusResponse resp = new StatusResponse(HttpStatus.SC_OK, "Accept Contact Invite successful");
            //getOwnNotification(user, resp);

            return new ResponseEvent<StatusResponse>(resp);
        } else {
            throw new InvalidUserSessionException();
        }
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> denyContactInvite(RequestEvent<DenyContactInviteRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing denyContactInvite request[" + requestEvent + "]");
        DenyContactInviteRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());
        Notification notif = notificationService.getNotification(user, req.getId());

        if (user != null) {
            List<Notification> notifications = new ArrayList<Notification>();
            // process old notif to remove
            notifications.add(notif);
            notificationService.removeNotifications(user, notifications);

            StatusResponse resp = new StatusResponse(HttpStatus.SC_OK, "Deny Contact Invite successful");
            //getOwnNotification(user, resp);

            return new ResponseEvent<StatusResponse>(resp);
        } else {
            throw new InvalidUserSessionException();
        }
    }

    public ResponseEvent<StatusResponse> processPoll(RequestEvent<PollRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing poll request[" + requestEvent + "]");
        User user = userService.getUser(requestEvent.getUserId());
        List<Message> messages = messageService.getMessages(user);

        StatusResponse resp = new StatusResponse(HttpStatus.SC_OK, "Poll successful");
        getOwnNotification(user, resp);

        if (messages != null && !messages.isEmpty()) {
            List<com.home911.httpchat.shared.model.Message> msgs =
                    new ArrayList<com.home911.httpchat.shared.model.Message>(messages.size());
            for (Message message : messages) {
                String name = message.getFrom().getUserInfo() != null &&
                        StringUtils.isEmpty(message.getFrom().getUserInfo().getFullname()) ?
                        message.getFrom().getUsername() : message.getFrom().getUserInfo().getFullname();
                msgs.add(new com.home911.httpchat.shared.model.Message(
                        new Contact(message.getFrom().getId(), name, message.getFrom().getPresence()), message.getText()));
            }
            resp.setMessages(msgs);
            messageService.removeMessages(user, messages);
        }

        if ((resp.getAlerts() == null || resp.getAlerts().isEmpty()) &&
                (resp.getMessages() == null || resp.getMessages().isEmpty())){
            resp = new StatusResponse(HttpStatus.SC_NO_CONTENT, "Poll successful");
        }

        return new ResponseEvent<StatusResponse>(resp);
    }

    public ResponseEvent<GetProfileResponse> processGetProfile(RequestEvent<GetProfileRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing getProfile request[" + requestEvent + "]");
        GetProfileRequest req = requestEvent.getRequest();
        User user = userService.getUser(req.getId());

        Profile profile = new Profile();

        // TODO handle bigger profile...
        profile.setFullname(user.getUserInfo().getFullname());

        return new ResponseEvent<GetProfileResponse>(new GetProfileResponse(HttpStatus.SC_OK,
                "Get Profile successful", profile));
    }

    public ResponseEvent<GetContactsResponse> processGetContacts(RequestEvent<GetContactsRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing getProfile request[" + requestEvent + "]");
        User user = userService.getUser(requestEvent.getUserId());

        List<Contact> ownContacts = new ArrayList<Contact>(user.getUserInfo().getContacts().size());
        // Adding "standard" contacts
        for (Ref<User> contactRef : user.getUserInfo().getContacts()) {
            User contact = contactRef.get();
            String name = StringUtils.isEmpty(contact.getUserInfo().getFullname()) ? contact.getUsername() :
                    contact.getUserInfo().getFullname();
            ownContacts.add(new Contact(contact.getId(), name, contact.getPresence()));
        }
        // Adding "pending" contacts as OFFLINE
        for (Ref<User> contactRef : user.getUserInfo().getPendingContacts()) {
            User contact = contactRef.get();
            String name = StringUtils.isEmpty(contact.getUserInfo().getFullname()) ? contact.getUsername() :
                    contact.getUserInfo().getFullname();
            ownContacts.add(new Contact(contact.getId(), name, Presence.OFFLINE));
        }

        if (ownContacts.isEmpty()) {
            ownContacts = null;
        }

        return new ResponseEvent<GetContactsResponse>(new GetContactsResponse(HttpStatus.SC_OK,
                "Get Contacts successful", ownContacts));
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processSendMessage(RequestEvent<SendMessageRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing sendMessage request[" + requestEvent + "]");
        SendMessageRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());
        User to = userService.getUser(req.getMessage().getTo());

        Message msg = new Message(to, user, req.getMessage().getText());
        if (Presence.ONLINE == to.getPresence()) {
            if (to.isAvailableForPush()) {
                notificationPusher.push(msg);
            } else {
                messageService.saveMessage(msg);
            }
            return new ResponseEvent<StatusResponse>(new StatusResponse(HttpStatus.SC_OK,
                    "Send Message successful"));
        } else {
            throw new UserOfflineException();
        }
    }

    @Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processRemoveContact(RequestEvent<RemoveContactRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing removeContact request[" + requestEvent + "]");
        RemoveContactRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());
        User contact = userService.getUser(req.getId());
        user.getUserInfo().removeContact(contact);
        userInfoService.saveUserInfo(user.getUserInfo());

        contact.getUserInfo().removeContact(user);
        contact.getUserInfo().addPendingContact(user);
        userInfoService.saveUserInfo(contact.getUserInfo());

        if (Presence.ONLINE == contact.getPresence()) {
            List<Notification> notifications = new ArrayList<Notification>();
            Notification notif = new Notification();
            notif.setOwner(contact);
            notif.setType(NotificationType.PRESENCE);
            notif.setReferer(user);
            notif.setData(Presence.OFFLINE.name());
            if (contact.isAvailableForPush()) {
                notificationPusher.push(notif);
            } else {
                notifications.add(notif);
                notificationService.addNotifications(notifications);
            }
        }

        return new ResponseEvent<StatusResponse>(new StatusResponse(HttpStatus.SC_OK,
                "Remove Contact successful"));
    }

    private EnumSet<ContactSearchFilterField> getContactSearchFilter(EnumSet<ContactFilterType> filterTypes) {
        EnumSet<ContactSearchFilterField> contactSearchFilter = EnumSet.noneOf(ContactSearchFilterField.class);
        for (ContactFilterType filterType : filterTypes) {
            switch (filterType) {
                case EMAIL:
                    contactSearchFilter.add(ContactSearchFilterField.EMAIL);
                    break;
                case FULLNAME:
                    contactSearchFilter.add(ContactSearchFilterField.FULLNAME);
                    break;
                case USERNAME:
                    contactSearchFilter.add(ContactSearchFilterField.USERNAME);
                    break;
            }
        }

        return contactSearchFilter;
    }

    private String generateToken(String username) {
        StringBuilder sb = new StringBuilder(username);
        sb.append("|").append(System.currentTimeMillis());

        try {
            byte[] bytes = sb.toString().getBytes(UTF8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);
            return Base64.encodeBytes(digest);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception.", e);
            throw new ServerErrorException();
        }
    }

    private String generateRegisterCode(User user) {
        StringBuilder sb = new StringBuilder("" + user.hashCode());
        sb.append("|").append(user.getId()).append("|").append(System.currentTimeMillis());

        try {
            return URLEncoder.encode(Base64.encodeBytes(sb.toString().getBytes()), UTF8);
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception.", e);
            throw new ServerErrorException();
        }
    }

    private Long parseRegisterCode(String code) {
        try {
            String decodedCode = new String(Base64.decode(code));
            StringTokenizer tokens = new StringTokenizer(decodedCode, "|");

            if (tokens.countTokens() == 3)
            {
                tokens.nextToken();
                return Long.valueOf(tokens.nextToken());
            } else {
                return null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception.", e);
            throw new ServerErrorException();
        }
    }

    private void getOwnNotification(User user, StatusResponse resp, boolean isLogin) {
        // get own notifications
        List<Notification> ownNotifications = notificationService.getNotifications(user);
        List<Notification> notifToRemove = new ArrayList<Notification>();
        if (ownNotifications != null && !ownNotifications.isEmpty()) {
            List<Alert> alerts = new ArrayList<Alert>(ownNotifications.size());
            for(Notification notif : ownNotifications) {
                if (NotificationType.PRESENCE == notif.getType()) {
                    notifToRemove.add(notif);
                }

                if ((isLogin && NotificationType.PRESENCE != notif.getType()) ||
                        (!isLogin)) {
                    User contactUsr = notif.getReferer();
                    String name = StringUtils.isEmpty(contactUsr.getUserInfo().getFullname()) ? contactUsr.getUsername() :
                            contactUsr.getUserInfo().getFullname();
                    alerts.add(new Alert(notif.getId(), notif.getType(), notif.getReferer().getId(),
                            new Contact(contactUsr.getId(), name, contactUsr.getPresence())));
                }
            }
            resp.setAlerts(alerts);
        }
        if (!notifToRemove.isEmpty()) {
            notificationService.removeNotifications(user, notifToRemove);
        }
    }

    private void getOwnNotification(User user, StatusResponse resp) {
        getOwnNotification(user, resp, false);
    }
}
