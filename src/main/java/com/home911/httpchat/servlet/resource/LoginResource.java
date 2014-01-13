package com.home911.httpchat.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.service.logic.BusinessLogic;
import com.home911.httpchat.servlet.event.RequestEvent;
import com.home911.httpchat.servlet.event.ResponseEvent;
import com.home911.httpchat.servlet.primitive.LoginRequest;
import com.home911.httpchat.servlet.primitive.LoginResponse;
import com.home911.httpchat.servlet.primitive.RegisterRequest;
import com.home911.httpchat.servlet.primitive.StatusResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/login")
public class LoginResource {
    private static final Logger LOGGER = Logger.getLogger(LoginResource.class.getCanonicalName());
    private static final String TOKEN_HEADER = "x-httpchat-token";
    private static final String USERID_HEADER = "x-httpchat-userid";

    private final BusinessLogic businessLogic;

    @Inject
    public LoginResource(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response login(LoginRequest request) {
        StatusResponse resp = null;
        if (StringUtils.isEmpty(request.getUsername())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[username].");
        } else if (StringUtils.isEmpty(request.getPassword())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[password].");
        }

        String token = null;
        Long userid = null;
        if (resp == null) {
            ResponseEvent<LoginResponse> respEvent =
                    businessLogic.processLogin(new RequestEvent<LoginRequest>(request));
            resp = respEvent.getResponse();
            userid = ((LoginResponse)resp).getUserId();
            token = ((LoginResponse)resp).getToken();
        }

        return Response.ok(resp).header(TOKEN_HEADER, token).header(USERID_HEADER, userid).build();
    }
}
