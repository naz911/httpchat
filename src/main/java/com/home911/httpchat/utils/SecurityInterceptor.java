package com.home911.httpchat.utils;

import com.google.inject.Inject;
import com.home911.httpchat.model.User;
import com.home911.httpchat.service.user.UserService;
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
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final String USERID_HEADER = "x-httpchat-userid";
    private static final String SECURE_PATH = "/secure/";

    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<Object>());;
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Missing token/userid to access this resource", 403, new Headers<Object>());;

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
            final String encodedUserIdToken = StringUtils.replaceOnce(authorization.get(0), AUTHENTICATION_SCHEME + " ", "");
            if (StringUtils.isEmpty(encodedUserIdToken)) {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }

            try {
                //Decode username and password
                String userIdToken = new String(Base64.decode(encodedUserIdToken));
                //Split username and password tokens
                final StringTokenizer tokenizer = new StringTokenizer(userIdToken, ":");
                final String userId = tokenizer.nextToken();
                final String token = tokenizer.nextToken();

                User user = userService.getUser(Long.valueOf(userId));

                if (user == null ||
                    !StringUtils.equals(user.getToken(), token)) {
                    requestContext.abortWith(ACCESS_DENIED);
                    return;
                } else {
                    requestContext.getHeaders().add(USERID_HEADER, userId);
                }
            } catch (IOException e) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }
        }
    }
}
