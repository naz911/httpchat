package com.home911.httpchat.server.utils;

import com.google.inject.Inject;
import com.home911.httpchat.server.model.User;
import com.home911.httpchat.server.service.user.UserService;
import com.home911.httpchat.shared.model.Presence;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.util.Base64;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter {
    private static final Logger LOGGER = Logger.getLogger(SecurityInterceptor.class.getCanonicalName());

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Custom";
    private static final String USERID_HEADER = "x-httpchat-userid";
    private static final String SECURE_PATH = "/secure/";

    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<Object>());
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Missing token/userid to access this resource", 403, new Headers<Object>());
    private static final ServerResponse GONE = new ServerResponse("User gone offline", 410, new Headers<Object>());

    private final UserService userService;

    @Inject
    public SecurityInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.log(Level.INFO, "Processing request with context[" + requestContext + "]");
        // first check if it is a secured request
        if (StringUtils.contains(requestContext.getUriInfo().getPath(), SECURE_PATH)) {
            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();

            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_HEADER);

            //If no authorization information present; block access
            if(authorization == null || authorization.isEmpty())
            {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }

            //Get encoded userid:token
            final String encodedToken = StringUtils.replaceOnce(authorization.get(0), AUTHENTICATION_SCHEME + " ", "");
            if (StringUtils.isEmpty(encodedToken)) {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }

            try {
                //Decode username and password
                String token = new String(Base64.decode(encodedToken));
                //Split username and password tokens
                final StringTokenizer tokenizer = new StringTokenizer(token, "|");
                final String userId = tokenizer.nextToken();
                final String ts = tokenizer.nextToken();

                if (isTimestampValid(ts)) {
                    User user = userService.getUser(Long.valueOf(userId));
                    if (user == null) {
                        requestContext.abortWith(ACCESS_FORBIDDEN);
                    } else if (Presence.OFFLINE == user.getPresence()) {
                        requestContext.abortWith(GONE);
                    } else {
                        requestContext.getHeaders().add(USERID_HEADER, userId);
                    }
                } else {
                    requestContext.abortWith(ACCESS_DENIED);
                }

            } catch (IOException e) {
                requestContext.abortWith(ACCESS_DENIED);
            }
            return;
        }
    }

    private boolean isTimestampValid(String ts) {
        return true;
    }
}
