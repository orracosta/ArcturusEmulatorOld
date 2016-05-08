package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CanCreateRoomComposer extends MessageComposer
{
    private final int count;

    public CanCreateRoomComposer(int count)
    {
        this.count = count;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CanCreateRoomComposer);

        int maxRooms = Emulator.getConfig().getInt("hotel.max.rooms.per.user");
        this.response.appendInt32(this.count >= maxRooms ? 1 : 0);
        this.response.appendInt32(maxRooms);
        this.response.appendString("");
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);

        return this.response;
    }
}
