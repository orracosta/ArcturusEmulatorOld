package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserPurchasePictureEvent extends UserEvent
{
    /**
     * The url of the picture.
     */
    public String url;

    /**
     * The room the picture was taken.
     */
    public int roomId;

    /**
     * The timestamp the picture was taken.
     */
    public int timestamp;

    /**
     * @param habbo The Habbo this event applies to.
     * @param url The url of the picture.
     * @param roomId The room the picture was taken.
     * @param timestamp The timestamp the picture was taken.
     */
    public UserPurchasePictureEvent(Habbo habbo, String url, int roomId, int timestamp)
    {
        super(habbo);

        this.url = url;
        this.roomId = roomId;
        this.timestamp = timestamp;
    }
}