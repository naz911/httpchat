package com.home911.httpchat.service.logic;

import com.google.inject.Inject;
import com.googlecode.objectify.Ref;
import com.home911.httpchat.exception.*;
import com.home911.httpchat.model.*;
import com.home911.httpchat.service.notification.NotificationService;
import com.home911.httpchat.service.user.UserService;
import com.home911.httpchat.service.userinfo.UserInfoService;
import com.home911.httpchat.servlet.event.RequestEvent;
import com.home911.httpchat.servlet.event.ResponseEvent;
import com.home911.httpchat.servlet.model.Alert;
import com.home911.httpchat.servlet.model.Contact;
import com.home911.httpchat.servlet.model.ContactFilterType;
import com.home911.httpchat.servlet.model.Profile;
import com.home911.httpchat.servlet.primitive.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.util.Base64;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BusinessLogicImpl implements BusinessLogic {
    private static final Logger LOGGER = Logger.getLogger(BusinessLogicImpl.class.getCanonicalName());

    private static final String UTF8 = "UTF-8";

    private final UserService userService;
    private final UserInfoService userInfoService;
    private final NotificationService notificationService;

    @Inject
    public BusinessLogicImpl(UserService userService, UserInfoService userInfoService,
                             NotificationService notificationService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
        this.notificationService = notificationService;
    }

    //@Transaction(TxnType.REQUIRED)
    public ResponseEvent<LoginResponse> processLogin(RequestEvent<LoginRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing login request[" + requestEvent + "]");
        LoginRequest req = requestEvent.getRequest();
        User user = userService.getUser(req.getUsername(), req.getPassword());

        if (user != null) {
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

            Profile profile = new Profile();
            profile.setFullname(user.getUserInfo().getFullname());

            // generate login response
            LoginResponse resp = new LoginResponse(HttpStatus.SC_OK, "Login successful", token, user.getId(), profile);

            if (user.getUserInfo().getContacts() != null && !user.getUserInfo().getContacts().isEmpty()) {
                List<Contact> ownContacts = new ArrayList<Contact>(user.getUserInfo().getContacts().size());
                List<Notification> notifications = new ArrayList<Notification>();

                for (Ref<User> contactRef : user.getUserInfo().getContacts()) {
                    User contact = contactRef.get();
                    String name = StringUtils.isEmpty(contact.getUserInfo().getFullname()) ? contact.getUsername() :
                            contact.getUserInfo().getFullname();
                    ownContacts.add(new Contact(contact.getId(), name, contact.getPresence()));

                    // generate presente notifications
                    if (Presence.ONLINE == contact.getPresence()) {
                        Notification notif = new Notification();
                        notif.setOwner(contact);
                        notif.setType(NotificationType.PRESENCE);
                        notif.setReferer(contactRef.get());
                        notif.setData(Presence.ONLINE.name());
                        notifications.add(notif);
                    }
                }

                // generate presence notifications
                if (!notifications.isEmpty()) {
                    notificationService.addNotifications(notifications);
                }

                resp.setContacts(ownContacts);
            }

            getOwnNotification(user, resp);

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
            userService.saveUser(user);

            if (user.getUserInfo().getContacts() != null && !user.getUserInfo().getContacts().isEmpty()) {
                List<Notification> notifications = new ArrayList<Notification>();
                for (Ref<User> contactRef : user.getUserInfo().getContacts()) {
                    User contact = contactRef.get();
                    if (Presence.ONLINE == contact.getPresence()) {
                        Notification notif = new Notification();
                        notif.setOwner(contact);
                        notif.setType(NotificationType.PRESENCE);
                        notif.setReferer(contactRef.get());
                        notif.setData(Presence.OFFLINE.name());
                        notifications.add(notif);
                    }
                }
                if (!notifications.isEmpty()) {
                    notificationService.addNotifications(notifications);
                }
            }

            // get own notifications
            List<Notification> ownNotifications = notificationService.getNotifications(user);
            if (ownNotifications != null && !ownNotifications.isEmpty()) {
                List<Notification> notificationsToRemove = new ArrayList<Notification>();
                for(Notification notif : ownNotifications) {
                    if (NotificationType.PROFILE == notif.getType() ||
                        NotificationType.PRESENCE == notif.getType()) {
                        notificationsToRemove.add(notif);
                    }
                }
                if (!notificationsToRemove.isEmpty()) {
                    notificationService.removeNotifications(notificationsToRemove);
                }
            }
        }

        return new ResponseEvent<StatusResponse>(new StatusResponse(HttpStatus.SC_OK, "Logout successful"));
    }

    //@Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processRegister(RequestEvent<RegisterRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing register request[" + requestEvent + "]");
        RegisterRequest req = requestEvent.getRequest();
        if (userService.exists(req.getUsername())) {
            throw new UserAlreadyExistException();
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setEmail(req.getEmail());
        userService.saveUser(user);

        // TODO send the email
        return new ResponseEvent<StatusResponse>(new StatusResponse(HttpStatus.SC_OK, "Register successful"));
    }

    //@Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processUpdateProfile(RequestEvent<UpdateProfileRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing updateProfile request[" + requestEvent + "]");
        UpdateProfileRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());

        if (user != null) {
            user.getUserInfo().setFullname(req.getProfile().getFullname());
            userInfoService.saveUserInfo(user.getUserInfo());

            if (user.getUserInfo().getContacts() != null && !user.getUserInfo().getContacts().isEmpty()) {
                List<Notification> notifications = new ArrayList<Notification>();
                for (Ref<User> contactRef : user.getUserInfo().getContacts()) {
                    User contact = contactRef.get();
                    if (Presence.ONLINE == contact.getPresence()) {
                        Notification notif = new Notification();
                        notif.setOwner(contact);
                        notif.setType(NotificationType.PROFILE);
                        notif.setReferer(contactRef.get());
                        notifications.add(notif);
                    }
                }

                if (!notifications.isEmpty()) {
                    notificationService.addNotifications(notifications);
                }
            }

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
            resp = new ContactSearchResponse(HttpStatus.SC_NO_CONTENT, "Search Contact succesful");
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

    //@Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> processContactInvite(RequestEvent<ContactInviteRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing contactInvite request[" + requestEvent + "]");
        ContactInviteRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());

        if (user != null) {
            UserInfo userInfo = user.getUserInfo();

            if (userInfo.getContacts() != null) {
                for (Ref<User> contact : userInfo.getContacts()) {
                    if (req.getId().equals(contact.get().getId())) {
                        // already in contact list..
                        throw new ContactAlreadyExistException();
                    }
                }
            }
            if (userInfo.getPendingContactIds() != null &&
                    user.getUserInfo().getPendingContactIds().contains(req.getId())) {
                throw new ContactInviteAlreadyExistException();
            }
            User contact = userService.getUser(req.getId());

            userInfo.addPendingContact(req.getId());
            userInfoService.saveUserInfo(userInfo);

            List<Notification> notifications = new ArrayList<Notification>();
            Notification notif = new Notification();
            notif.setOwner(contact);
            notif.setType(NotificationType.CONTACT_INVITE);
            notif.setReferer(user);
            notifications.add(notif);
            notificationService.addNotifications(notifications);

            StatusResponse resp = new StatusResponse(HttpStatus.SC_OK, "Contact Invite successful");
            //getOwnNotification(user, resp);

            return new ResponseEvent<StatusResponse>(resp);
        } else {
            throw new InvalidUserSessionException();
        }
    }

    //@Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> acceptContactInvite(RequestEvent<AcceptContactInviteRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing acceptContactInvite request[" + requestEvent + "]");
        AcceptContactInviteRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());
        Notification notif = notificationService.getNotification(req.getId());
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
            notificationService.removeNotifications(notifications);

            StatusResponse resp = new StatusResponse(HttpStatus.SC_OK, "Accept Contact Invite successful");
            //getOwnNotification(user, resp);

            return new ResponseEvent<StatusResponse>(resp);
        } else {
            throw new InvalidUserSessionException();
        }
    }

    //@Transaction(TxnType.REQUIRED)
    public ResponseEvent<StatusResponse> denyContactInvite(RequestEvent<DenyContactInviteRequest> requestEvent) {
        LOGGER.log(Level.INFO, "Processing denyContactInvite request[" + requestEvent + "]");
        DenyContactInviteRequest req = requestEvent.getRequest();
        User user = userService.getUser(requestEvent.getUserId());
        Notification notif = notificationService.getNotification(req.getId());

        if (user != null) {
            List<Notification> notifications = new ArrayList<Notification>();
            // process old notif to remove
            notifications.add(notif);
            notificationService.removeNotifications(notifications);

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

        StatusResponse resp = new StatusResponse(HttpStatus.SC_OK, "Poll successful");
        getOwnNotification(user, resp);

        if (resp.getAlerts() == null || resp.getAlerts().isEmpty()) {
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

        List<Contact> ownContacts = null;
        if (user.getUserInfo().getContacts() != null && !user.getUserInfo().getContacts().isEmpty()) {
            ownContacts = new ArrayList<Contact>(user.getUserInfo().getContacts().size());

            for (Ref<User> contactRef : user.getUserInfo().getContacts()) {
                User contact = contactRef.get();
                String name = StringUtils.isEmpty(contact.getUserInfo().getFullname()) ? contact.getUsername() :
                        contact.getUserInfo().getFullname();
                ownContacts.add(new Contact(contact.getId(), name, contact.getPresence()));
            }
        }

        return new ResponseEvent<GetContactsResponse>(new GetContactsResponse(HttpStatus.SC_OK,
                "Get Contacts successful", ownContacts));
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
            throw new ServerErrorException();
        }
    }

    private void getOwnNotification(User user, StatusResponse resp) {
        // get own notifications
        List<Notification> ownNotifications = notificationService.getNotifications(user);
        if (ownNotifications != null && !ownNotifications.isEmpty()) {
            List<Alert> alerts = new ArrayList<Alert>(ownNotifications.size());
            for(Notification notif : ownNotifications) {
                alerts.add(new Alert(notif.getId(), notif.getType(), user.getId(), notif.getData()));
            }
            resp.setAlerts(alerts);
        }
    }
}