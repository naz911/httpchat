package com.home911.httpchat.server.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.server.service.logic.BusinessLogic;
import com.home911.httpchat.server.servlet.event.RequestEvent;
import com.home911.httpchat.server.servlet.event.ResponseEvent;
import com.home911.httpchat.server.servlet.primitive.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/secure/contact")
public class ContactResource {
    private static final Logger LOGGER = Logger.getLogger(ContactResource.class.getCanonicalName());

    private final BusinessLogic businessLogic;

    @Inject
    public ContactResource(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    @POST
    @Path("/{cid}/invite")
    @Produces({MediaType.APPLICATION_JSON})
    public Response inviteContact(@HeaderParam("x-httpchat-userid") Long id, @PathParam("cid") Long cid) {
        ResponseEvent<StatusResponse> respEvent =
                businessLogic.processContactInvite(new RequestEvent<ContactInviteRequest>(id,
                        new ContactInviteRequest(cid)));

        return Response.ok(respEvent.getResponse()).build();
    }

    @DELETE
    @Path("/{cid}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response removeContact(@HeaderParam("x-httpchat-userid") Long id, @PathParam("cid") Long cid) {
        ResponseEvent<StatusResponse> respEvent =
                businessLogic.processRemoveContact(new RequestEvent<RemoveContactRequest>(id,
                        new RemoveContactRequest(cid)));

        return Response.ok(respEvent.getResponse()).build();
    }

    @POST
    @Path("/invite/{iid}/accept")
    @Produces({MediaType.APPLICATION_JSON})
    public Response acceptContactInvite(@HeaderParam("x-httpchat-userid") Long id, @PathParam("iid") Long iid) {
        ResponseEvent<StatusResponse> respEvent =
                businessLogic.acceptContactInvite(new RequestEvent<AcceptContactInviteRequest>(id,
                        new AcceptContactInviteRequest(iid)));

        return Response.ok(respEvent.getResponse()).build();
    }

    @POST
    @Path("/invite/{iid}/deny")
    @Produces({MediaType.APPLICATION_JSON})
    public Response denyContactInvite(@HeaderParam("x-httpchat-userid") Long id, @PathParam("iid") Long iid) {
        ResponseEvent<StatusResponse> respEvent =
                businessLogic.denyContactInvite(new RequestEvent<DenyContactInviteRequest>(id,
                        new DenyContactInviteRequest(iid)));

        return Response.ok(respEvent.getResponse()).build();
    }
}
