package com.eu.habbo.plugin.events.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

import java.util.ArrayList;

/**
 * Created on 9-11-2015 21:03.
 */
public abstract class NavigatorRoomsEvent extends UserEvent
{
    /**
     * The rooms displayed.
     */
    public final ArrayList<Room> rooms;

    /**
     * @param habbo The Habbo this event applies to.
     * @param rooms The rooms displayed.
     */
    public NavigatorRoomsEvent(Habbo habbo, ArrayList<Room> rooms)
    {
        super(habbo);

        this.rooms = rooms;
    }
}
