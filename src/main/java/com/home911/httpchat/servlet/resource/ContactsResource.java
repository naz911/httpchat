package com.home911.httpchat.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.service.logic.BusinessLogic;
import com.home911.httpchat.servlet.event.RequestEvent;
import com.home911.httpchat.servlet.event.ResponseEvent;
import com.home911.httpchat.servlet.model.ContactFilterType;
import com.home911.httpchat.servlet.primitive.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.EnumSet;
import java.util.logging.Logger;

@Path("/secure/contacts")
public class ContactsResource {
    private static final Logger LOGGER = Logger.getLogger(ContactsResource.class.getCanonicalName());

    private final BusinessLogic businessLogic;

    @Inject
    public ContactsResource(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getContacts(@HeaderParam("x-httpchat-userid") Long id) {

        ResponseEvent<GetContactsResponse> respEvent =
                businessLogic.processGetContacts(new RequestEvent<GetContactsRequest>(id,
                        new GetContactsRequest()));

        return Response.ok(respEvent.getResponse()).build();
    }

    @GET
    @Path("/s")
    @Produces({MediaType.APPLICATION_JSON})
    public Response searchContacts(@HeaderParam("x-httpchat-userid") Long id,
                                   @QueryParam("filterTypes") String filterTypes,
                                   @QueryParam("filterValue") String filterValue,
                                   @QueryParam("offset") int offset,
                                   @QueryParam("limit") int limit) {
        StatusResponse resp = null;
        if (StringUtils.isEmpty(filterTypes)) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[filterTypes].");
        } else if (StringUtils.isEmpty(filterValue)) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[filterValue].");
        } else if (limit == 0) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[limit].");
        }

        if (resp == null) {
            ResponseEvent<ContactSearchResponse> respEvent =
                    businessLogic.processSearchContacts(new RequestEvent<ContactSearchRequest>(id,
                            new ContactSearchRequest(parseCommaSeparatedList(filterTypes), filterValue, offset, limit)));
            resp = respEvent.getResponse();
        }

        return Response.ok(resp).build();
    }

    private EnumSet<ContactFilterType> parseCommaSeparatedList(String filterTypesStr) {
        EnumSet<ContactFilterType> filterTypes = EnumSet.noneOf(ContactFilterType.class);
        String[] filters = filterTypesStr.split(",");
        for (String filter : filters) {
            filterTypes.add(ContactFilterType.valueOf(filter));
        }
        return filterTypes;
    }
}
