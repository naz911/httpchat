package com.home911.httpchat.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.service.logic.BusinessLogic;
import com.home911.httpchat.servlet.event.RequestEvent;
import com.home911.httpchat.servlet.event.ResponseEvent;
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
    public Response register(RegisterRequest request) {
        StatusResponse resp = null;
        if (StringUtils.isEmpty(request.getUsername())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[username].");
        } else if (StringUtils.isEmpty(request.getPassword())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[password].");
        }  else if (StringUtils.isEmpty(request.getEmail())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[email].");
        }

        if (resp == null) {
            ResponseEvent<StatusResponse> respEvent =
                    businessLogic.processRegister(new RequestEvent<RegisterRequest>(request));
            resp = respEvent.getResponse();
        }

        return Response.ok(resp).build();
    }
}
