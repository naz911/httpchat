package com.home911.httpchat.server.utils;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

public final class GsonUtil {
    private static final GsonUtil INSTANCE = new GsonUtil();

    private Gson gson;

    private GsonUtil() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                .registerTypeAdapter(DateTime.class, new DateTimeSerializer()).create();
    }

    public static GsonUtil getInstance() {
        return INSTANCE;
    }

    public Gson getGson() {
        return gson;
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
