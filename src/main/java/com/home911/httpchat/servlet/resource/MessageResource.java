package com.home911.httpchat.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.service.logic.BusinessLogic;
import com.home911.httpchat.servlet.event.RequestEvent;
import com.home911.httpchat.servlet.event.ResponseEvent;
import com.home911.httpchat.servlet.model.Message;
import com.home911.httpchat.servlet.primitive.*;
import org.apache.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/secure/message")
public class MessageResource {
    private static final Logger LOGGER = Logger.getLogger(MessageResource.class.getCanonicalName());

    private final BusinessLogic businessLogic;

    @Inject
    public MessageResource(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    @POST
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response sendMessage(@HeaderParam("x-httpchat-userid") Long id, Message message) {

        StatusResponse resp = null;

        if (message == null) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required message information.");
        } else if (message.getTo() == null) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[to].");
        } else if (message.getText() == null) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[text].");
        }

        if (resp == null) {
            ResponseEvent<StatusResponse> respEvent =
                    businessLogic.processSendMessage(new RequestEvent<SendMessageRequest>(id,
                            new SendMessageRequest(message)));
            resp = respEvent.getResponse();
        }

        return Response.ok(resp).build();
    }
}
