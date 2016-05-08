package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;

import java.util.Map;

/**
 * Created on 13-12-2014 16:47.
 */
public class RoomRightsListComposer extends MessageComposer
{
    private final Room room;

    public RoomRightsListComposer(Room room)
    {
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomRightsListComposer);
        this.response.appendInt32(this.room.getId());

        THashMap<Integer, String> rightsMap = this.room.getUsersWithRights();

        this.response.appendInt32(rightsMap.size());

        for(Map.Entry<Integer, String> set : rightsMap.entrySet())
        {
            this.response.appendInt32(set.getKey());
            this.response.appendString(set.getValue());
        }

        return this.response;
    }
}
