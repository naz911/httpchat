package com.home911.httpchat;

import com.home911.httpchat.exception.HttpChatException;
import com.home911.httpchat.servlet.primitive.StatusResponse;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpChatExceptionsMapper {
    private static final Logger LOGGER = Logger.getLogger(HttpChatExceptionsMapper.class.getCanonicalName());

    @Provider
    public static class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException>
    {
        @Override
        public Response toResponse(final WebApplicationException exception)
        {
            return Response.status(exception.getResponse().getStatus()).entity(exception.getResponse().getEntity()).build();
        }
    }

    @Provider
    @Produces(MediaType.APPLICATION_JSON)
    public static class HttpChatExceptionMapper implements ExceptionMapper<HttpChatException>
    {
        @Override
        public Response toResponse(final HttpChatException exception)
        {
            return Response.ok(new StatusResponse(exception.getStatus(), exception.getDescription())).build();
        }
    }

    @Provider
    public static class AllExceptionMapper implements ExceptionMapper<Exception>
    {
        @Override
        public Response toResponse(final Exception exception)
        {
            HttpChatExceptionsMapper.LOGGER.log(Level.WARNING, "Internal error.", exception);
            return Response.serverError().build();
        }
    }
}
