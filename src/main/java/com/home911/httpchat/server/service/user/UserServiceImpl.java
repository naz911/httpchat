package com.home911.httpchat.server.service.user;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.SimpleQuery;
import com.home911.httpchat.server.model.ContactSearchFilterField;
import com.home911.httpchat.server.model.User;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getCanonicalName());

    public UserServiceImpl() {
        ObjectifyService.register(User.class);
    }

    public User getUser(String username, String password) {
        LOGGER.info("Getting user for username:[" + username + "] and password: [*******]");
        SimpleQuery<User> query = ofy().transactionless().load().type(User.class).filter("username", username)
                .filter("password", password).limit(1);
        LoadResult<User> result = query.first();
        User user = null;
        if (result != null) {
            user = result.now();
        }

        if (user != null) {
            user = getUser(user.getId());
        }
        return user;
    }

    public boolean exists(String username) {
        LOGGER.info("Getting user for username:[" + username + "]");
        SimpleQuery<User> query = ofy().transactionless().load().type(User.class).filter("username", username).limit(1);
        return query.count() >= 1;
    }

    public User getUser(Long id) {
        LOGGER.info("Getting user for id:[" + id + "]");
        User user = ofy().load().type(User.class).id(id).now();
        return user;
    }

    public void saveUser(User user) {
        LOGGER.info("Saving user: " + user);
        ofy().save().entity(user).now();
    }

    public List<User> getUsers(EnumSet<ContactSearchFilterField> filters, Object value, int offset, int limit) {
        LOGGER.info("Getting users for filters: " + filters + " value:" + value);
        List<User> users = new ArrayList<User>();
        Query<User> query = ofy().load().type(User.class);

        if (filters != null && !filters.isEmpty()) {
            for (ContactSearchFilterField filter : filters) {
                query = query.filter(filter.getFilter() + " >=", value)
                             .filter(filter.getFilter() + " <", value + "\uFFFD");
            }
        }
        query = query.offset(offset).limit(limit);

        QueryResultIterator<User> iterator = query.iterator();
        while (iterator.hasNext()) {
            users.add(iterator.next());
        }

        return users;
    }
}
