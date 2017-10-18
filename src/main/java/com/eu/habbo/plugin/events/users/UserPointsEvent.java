package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserPointsEvent extends UserEvent
{
    /**
     * Amount of points that will be added or removed.
     */
    public int points;

    /**
     * Points type.
     */
    public int type;

    /**
     * This event is triggered when points are added or removed from a user.
     * @param habbo The Habbo this event applies to.
     * @param amount Amount of points that will be added or removed.
     * @param type Points type.
     */
    public UserPointsEvent(Habbo habbo, int points, int type)
    {
        super(habbo);

        this.points = points;
        this.type   = type;
    }
}