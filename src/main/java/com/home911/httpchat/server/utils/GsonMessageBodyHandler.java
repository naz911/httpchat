package com.home911.httpchat.server.utils;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class GsonMessageBodyHandler implements MessageBodyWriter<Object>,
        MessageBodyReader<Object> {

    private static final String UTF_8 = "UTF-8";
    private static final Logger LOGGER = Logger.getLogger(GsonMessageBodyHandler.class.getCanonicalName());

    private Gson gson;

    private Gson getGson() {
        if (gson == null) {
            final GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                    .registerTypeAdapter(DateTime.class, new DateTimeSerializer()).create();
        }
        return gson;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        InputStreamReader streamReader = null;
        try {
            streamReader = new InputStreamReader(entityStream, UTF_8);
            Type jsonType;
            if (type.equals(genericType)) {
                jsonType = type;
            } else {
                jsonType = genericType;
            }
            return getGson().fromJson(streamReader, jsonType);
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Problem reading the stream.", e);
            return null;
        } finally {
            try {
                if (streamReader!=null) {
                    streamReader.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Problem closing the stream.", e);
            }
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
        try {
            Type jsonType;
            if (type.equals(genericType)) {
                jsonType = genericType;
            } else {
                jsonType = type;
            }
            getGson().toJson(object, jsonType, writer);
        } finally {
            writer.close();
        }
    }

    private static class DateTimeDeserializer implements JsonDeserializer<DateTime> {
        public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new DateTime(ISODateTimeFormat.dateParser().parseDateTime(json.getAsString()), DateTimeZone.UTC);
        }
    }

    private static class DateTimeSerializer implements JsonSerializer<DateTime> {
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(ISODateTimeFormat.dateTimeNoMillis().print(src));
        }
    }
}
