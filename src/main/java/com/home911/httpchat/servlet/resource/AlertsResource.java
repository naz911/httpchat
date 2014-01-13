package com.home911.httpchat.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.service.logic.BusinessLogic;
import com.home911.httpchat.servlet.event.RequestEvent;
import com.home911.httpchat.servlet.event.ResponseEvent;
import com.home911.httpchat.servlet.primitive.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/secure/alerts")
public class AlertsResource {
    private static final Logger LOGGER = Logger.getLogger(AlertsResource.class.getCanonicalName());

    private final BusinessLogic businessLogic;

    @Inject
    public AlertsResource(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response poll(@HeaderParam("x-httpchat-userid") Long id) {
        ResponseEvent<StatusResponse> respEvent =
                businessLogic.processPoll(new RequestEvent<PollRequest>(id,
                        new PollRequest()));

        return Response.ok(respEvent.getResponse()).build();
    }
}
