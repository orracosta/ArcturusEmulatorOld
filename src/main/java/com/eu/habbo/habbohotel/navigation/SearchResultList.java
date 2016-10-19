package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomState;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResultList implements ISerialize
{
    public final int order;
    public final String code;
    public final String query;
    public final SearchAction action;
    public final SearchMode mode;
    public final boolean hidden;
    public final List<Room> rooms;
    public final boolean filter;
    public final boolean showInvisible;

    public SearchResultList(int order, String code, String query, SearchAction action, SearchMode mode, boolean hidden, List<Room> rooms, boolean filter, boolean showInvisible)
    {
        this.order = order;
        this.code = code;
        this.query = query;
        this.action = action;
        this.mode = mode;
        this.rooms = rooms;
        this.hidden = hidden;
        this.filter = filter;
        this.showInvisible = showInvisible;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString(this.code); //Search Code
        message.appendString(this.query); //Text
        message.appendInt32(this.action.type); //Action Allowed (0 (Nothing), 1 (More Results), 2 (Go Back))
        message.appendBoolean(this.hidden); //Closed
        message.appendInt32(this.mode.type); //Display Mode (0 (List), 1 (Thumbnails), 2 (Thumbnail no choice))

        if (!this.showInvisible)
        {
            List<Room> toRemove = new ArrayList<Room>();
            for (Room room : this.rooms)
            {
                if (room.getState() == RoomState.INVISIBLE)
                {
                    toRemove.add(room);
                }
            }

            this.rooms.removeAll(toRemove);
        }

        message.appendInt32(this.rooms.size());

        Collections.sort(this.rooms);
        for (Room room : this.rooms)
        {
            room.serialize(message);
        }
    }
}