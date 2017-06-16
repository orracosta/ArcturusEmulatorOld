package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUserNameChangedComposer extends MessageComposer
{
    private final RoomUnit roomUnit;
    private final Habbo habbo;

    public RoomUserNameChangedComposer(RoomUnit roomUnit, Habbo habbo)
    {
        this.roomUnit = roomUnit;
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserNameChangedComposer);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.habbo.getHabboInfo().getId());
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        return this.response;
    }
}