package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 22-10-2014 16:55.
 */
public class RoomChatSettingsComposer extends MessageComposer
{
    private final Room room;

    public RoomChatSettingsComposer(Room room)
    {
        this.room = room;
    }
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomChatSettingsComposer);
        this.response.appendInt32(this.room.getChatMode());
        this.response.appendInt32(this.room.getChatWeight());
        this.response.appendInt32(this.room.getChatSpeed());
        this.response.appendInt32(this.room.getChatDistance());
        this.response.appendInt32(this.room.getChatProtection());
        return this.response;
    }
}
