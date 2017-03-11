package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserPublishPictureEvent extends UserEvent
{
    /**
     * The URL the picture gets saved to.
     */
    public String URL;

    /**
     * The timestamp that the picture was taken.
     */
    public int timestamp;

    /**
     * @param habbo The Habbo this event applies to.
     * @param url The URL the picture gets saved to.
     * @param timestamp The timestamp that the picture was taken.
     */
    public UserPublishPictureEvent(Habbo habbo, String url, int timestamp)
    {
        super(habbo);
        URL = url;
        this.timestamp = timestamp;
    }
}