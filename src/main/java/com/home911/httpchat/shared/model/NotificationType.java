package com.home911.httpchat.shared.model;

public enum NotificationType {
    PRESENCE, CONTACT_INVITE;

    /*
    public static class NotificationTypeDeserializer implements JsonDeserializer<NotificationType> {
        public NotificationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return valueOf(json.getAsJsonPrimitive().getAsString());
        }
    }

    public static class NotificationTypeSerializer implements JsonSerializer<Presence> {
        public JsonElement serialize(Presence src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
    */
}
