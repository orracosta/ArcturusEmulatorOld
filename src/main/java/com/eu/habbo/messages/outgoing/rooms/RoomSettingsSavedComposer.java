package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 3-4-2015 23:11.
 */
public class RoomSettingsSavedComposer extends MessageComposer
{
    private final Room room;

    public RoomSettingsSavedComposer(Room room)
    {
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomSettingsSavedComposer);
        this.response.appendInt32(this.room.getId());
        return this.response;
    }
}
