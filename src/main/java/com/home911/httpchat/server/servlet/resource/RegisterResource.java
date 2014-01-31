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
import org.jboss.resteasy.spi.ResteasyUriInfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public Response confirm(@QueryParam("code") String code) {
        StatusResponse resp = null;
        if (StringUtils.isEmpty(code)) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[code].");
        }

        if (resp == null) {
            ResponseEvent<StatusResponse> respEvent = businessLogic.processConfirmRegister(
                    new RequestEvent<ConfirmRegisterRequest>(new ConfirmRegisterRequest(code)));
            resp = respEvent.getResponse();
        }

        return Response.ok(resp).build();
    }
}
