package com.eu.habbo.messages.rcon;

import com.google.gson.JsonObject;

/**
 * Created on 29-8-2015 10:01.
 */
public abstract class RCONMessage<T>
{
    /**
     * The class the JSON gets mapped to.
     */
    public final Class<T> type;

    public RCONMessage(Class<T> type)
    {
        this.type = type;
    }

    /**
     * @param object The mapped JSON object.
     * @return A string that gets send to the RCON Client. Can be anything (JSON / XML) whatever you want.
     */
    public abstract String handle(T object);
}
