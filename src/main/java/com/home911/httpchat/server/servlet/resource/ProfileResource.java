package com.home911.httpchat.server.servlet.resource;

import com.google.inject.Inject;
import com.home911.httpchat.server.service.logic.BusinessLogic;
import com.home911.httpchat.server.servlet.event.RequestEvent;
import com.home911.httpchat.server.servlet.event.ResponseEvent;
import com.home911.httpchat.shared.model.Profile;
import com.home911.httpchat.shared.model.ProfileFilterType;
import com.home911.httpchat.server.servlet.primitive.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/secure/profile")
public class ProfileResource {
    private static final Logger LOGGER = Logger.getLogger(ProfileResource.class.getCanonicalName());

    private final BusinessLogic businessLogic;

    @Inject
    public ProfileResource(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMyProfile(@HeaderParam("x-httpchat-userid") Long id,
                                 @QueryParam("filterType") ProfileFilterType filter) {
        StatusResponse resp = null;

        if (filter == null) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[filterType].");
        }

        if (resp == null) {
            ResponseEvent<GetProfileResponse> respEvent =
                    businessLogic.processGetProfile(new RequestEvent<GetProfileRequest>(
                            new GetProfileRequest(id, filter)));
            resp = respEvent.getResponse();
        }

        return Response.ok(resp).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response getProfile(@HeaderParam("x-httpchat-userid") Long id,
                               @PathParam("id") Long profileId,
                               @QueryParam("filterType") ProfileFilterType filter) {
        StatusResponse resp = null;

        if (filter == null) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[filterType].");
        }

        if (resp == null) {
            ResponseEvent<GetProfileResponse> respEvent =
                    businessLogic.processGetProfile(new RequestEvent<GetProfileRequest>(
                            new GetProfileRequest(profileId, filter)));
            resp = respEvent.getResponse();
        }

        return Response.ok(resp).build();
    }

    @POST
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateProfile(@HeaderParam("x-httpchat-userid") Long id, Profile profile) {
        StatusResponse resp = null;
        if (profile == null) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required profile information.");
        } else if (StringUtils.isEmpty(profile.getFullname())) {
            resp = new StatusResponse(HttpStatus.SC_BAD_REQUEST, "Missing required field[username].");
        }

        if (resp == null) {
            ResponseEvent<StatusResponse> respEvent =
                    businessLogic.processUpdateProfile(new RequestEvent<UpdateProfileRequest>(id,
                            new UpdateProfileRequest(profile)));
            resp = respEvent.getResponse();
        }

        return Response.ok(resp).build();
    }
}
