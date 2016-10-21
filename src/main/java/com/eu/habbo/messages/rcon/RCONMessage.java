package com.eu.habbo.messages.rcon;

import java.lang.reflect.Type;

import com.google.gson.*;

public abstract class RCONMessage<T>
{
    public final static int STATUS_OK = 0;
    public final static int STATUS_ERROR = 1;
    public final static int HABBO_NOT_FOUND = 2;
    public final static int ROOM_NOT_FOUND = 3;
    public final static int SYSTEM_ERROR = 4;

    /**
     * The class the JSON gets mapped to.
     */
    public final Class<T> type;

    public RCONMessage(Class<T> type)
    {
        this.type = type;
    }

    /**
     *
     * @param gson
     * @param json The mapped JSON object.
     * @return A string that gets send to the RCON Client. Can be anything (JSON / XML) whatever you want.
     */
    public abstract void handle(Gson gson, T json);

    public int status = STATUS_OK;
    public String message = "";

    public static class RCONMessageSerializer implements JsonSerializer<RCONMessage>
    {
        public JsonElement serialize(final RCONMessage rconMessage, final Type type, final JsonSerializationContext context)
        {
            JsonObject result = new JsonObject();
            result.add("status", new JsonPrimitive(rconMessage.status));
            result.add("message", new JsonPrimitive(rconMessage.message));
            return result;
        }
    }
}
