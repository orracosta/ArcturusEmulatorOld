package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 15-10-2014 16:22.
 */
public class RoomUserUpdateNameComposer extends MessageComposer
{
    private final RoomUnit roomUnit;
    private final String name;

    public RoomUserUpdateNameComposer(RoomUnit roomUnit, String name)
    {
        this.roomUnit = roomUnit;
        this.name = name;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserUpdateNameComposer);
        this.response.appendInt32(this.roomUnit.getId());
        this.response.appendString(this.name);
        this.response.appendInt32(0);
        return this.response;
    }
}
