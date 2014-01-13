package com.home911.httpchat.model;

import com.google.gson.*;

import java.lang.reflect.Type;

public enum Presence {
    ONLINE, OFFLINE;

    public static class PresenceDeserializer implements JsonDeserializer<Presence> {
        public Presence deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return valueOf(json.getAsJsonPrimitive().getAsString());
        }
    }

    public static class PresenceSerializer implements JsonSerializer<Presence> {
        public JsonElement serialize(Presence src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}
