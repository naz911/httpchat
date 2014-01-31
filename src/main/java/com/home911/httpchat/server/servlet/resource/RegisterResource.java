package com.home911.httpchat.server.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.server.service.logic.BusinessLogic;
import com.home911.httpchat.server.servlet.event.RequestEvent;
import com.home911.httpchat.server.servlet.event.ResponseEvent;
import com.home911.httpchat.server.servlet.primitive.ConfirmRegisterRequest;
import com.home911.httpchat.server.servlet.primitive.RegisterRequest;
import com.home911.httpchat.server.servlet.primitive.RegisterResponse;
import com.home911.httpchat.server.servlet.primitive.StatusResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/register")
public class RegisterResource {
    private static final Logger LOGGER = Logger.getLogger(RegisterResource.class.getCanonicalName());

    private final BusinessLogic businessLogic;

    @Inject
    public RegisterResource(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response register(RegisterRequest request, @Context HttpServletRequest httpRequest) {
        StatusResponse resp = null;
        if (StringUtils.isEmpty(request.getUsername())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[username].");
        } else if (StringUtils.isEmpty(request.getPassword())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[password].");
        }  else if (StringUtils.isEmpty(request.getEmail())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[email].");
        }

        if (resp == null) {
            StringBuilder confirmUrl = new StringBuilder(httpRequest.getRequestURL().toString());
            confirmUrl.append("/confirm?code=");
            request.setConfirmUrl(confirmUrl.toString());
            ResponseEvent<RegisterResponse> respEvent =
                    businessLogic.processRegister(new RequestEvent<RegisterRequest>(request));
            resp = respEvent.getResponse();
        }

        return Response.ok(resp).build();
    }

    @GET
    @Path("/confirm")
    @Produces({MediaType.APPLICATION_JSON})
    public Response confirm(@QueryParam("code") String code, @Context HttpServletRequest httpRequest) {
        StatusResponse resp = null;
        if (StringUtils.isEmpty(code)) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[code].");
        }

        if (resp == null) {
            ResponseEvent<StatusResponse> respEvent = businessLogic.processConfirmRegister(
                    new RequestEvent<ConfirmRegisterRequest>(new ConfirmRegisterRequest(code)));
            resp = respEvent.getResponse();
        }

        if (resp.getStatus().getCode() == 200) {
            URI redirect = getBaseUri(httpRequest);
            if (redirect != null) {
                return Response.temporaryRedirect(redirect).build();
            } else {
                return Response.ok(resp).build();
            }
        } else {
            return Response.ok(resp).build();
        }
    }

    private URI getBaseUri(HttpServletRequest httpRequest) {
        StringBuilder url = new StringBuilder(httpRequest.getScheme());
        url.append("://").append(httpRequest.getServerName());
        if ( httpRequest.getServerPort() != 80 && httpRequest.getServerPort() != 443 ) {
            url.append(":").append(httpRequest.getServerPort());
        }
        try {
            return new URI(url.toString());
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception.", e);
            return null;
        }
    }
}
