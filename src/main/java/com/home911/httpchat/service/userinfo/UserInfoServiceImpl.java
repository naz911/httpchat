package com.home911.httpchat.service.userinfo;

import com.googlecode.objectify.ObjectifyService;
import com.home911.httpchat.model.UserInfo;

import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class UserInfoServiceImpl implements UserInfoService {
    private static final Logger LOGGER = Logger.getLogger(UserInfoServiceImpl.class.getCanonicalName());

    public UserInfoServiceImpl() {
        ObjectifyService.register(UserInfo.class);
    }

    public UserInfo saveUserInfo(UserInfo userInfo) {
        LOGGER.info("Saving user: " + userInfo);
        ofy().save().entity(userInfo).now();
        return userInfo;
    }
}
