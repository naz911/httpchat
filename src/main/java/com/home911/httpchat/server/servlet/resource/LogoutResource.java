package com.home911.httpchat.server.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.server.service.logic.BusinessLogic;
import com.home911.httpchat.server.servlet.event.RequestEvent;
import com.home911.httpchat.server.servlet.event.ResponseEvent;
import com.home911.httpchat.server.servlet.primitive.LogoutRequest;
import com.home911.httpchat.server.servlet.primitive.StatusResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/secure/logout")
public class LogoutResource {
    private static final Logger LOGGER = Logger.getLogger(LogoutResource.class.getCanonicalName());
    private static final String USERID_HEADER = "x-httpchat-userid";

    private final BusinessLogic businessLogic;

    @Inject
    public LogoutResource(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response logout(@HeaderParam("x-httpchat-userid") Long id) {
        ResponseEvent<StatusResponse> respEvent =
                businessLogic.processLogout(new RequestEvent<LogoutRequest>(id, new LogoutRequest()));

        return Response.ok(respEvent.getResponse()).build();
    }
}
