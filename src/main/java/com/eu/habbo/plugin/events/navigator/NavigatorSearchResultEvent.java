package com.eu.habbo.plugin.events.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.ArrayList;

public class NavigatorSearchResultEvent extends NavigatorRoomsEvent
{
    /**
     * Prefix used to search. e.g. owner:
     */
    public final String prefix;

    /**
     * The query that has been used to search for rooms.
     */
    public final String query;

    /**
     * Triggered whenever a user searches for rooms in the navigator.
     * @param habbo The Habbo this event applies to.
     * @param prefix Prefix used to search. e.g. owner:
     * @param query The query that has been used to search for rooms.
     */
    public NavigatorSearchResultEvent(Habbo habbo, String prefix, String query, ArrayList<Room> rooms)
    {
        super(habbo, rooms);

        this.prefix = prefix;
        this.query = query;
    }
}
