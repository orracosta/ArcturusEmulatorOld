package com.eu.habbo.habbohotel.guides;

/**
 * Created on 11-10-2015 00:21.
 */
public class GuideChatMessage
{
    public final int userId;
    public final String message;
    public final int timestamp;

    public GuideChatMessage(int userId, String message, int timestamp)
    {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }
}
