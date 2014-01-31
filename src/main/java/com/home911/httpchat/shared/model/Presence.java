package com.home911.httpchat.shared.model;

import java.io.Serializable;

public enum Presence implements Serializable {
    ONLINE, OFFLINE;

    /*
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
    }*/
}
