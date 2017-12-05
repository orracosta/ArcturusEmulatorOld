package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUserRespectComposer extends MessageComposer
{
    private final Habbo habbo;

    public RoomUserRespectComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserRespectComposer);
        this.response.appendInt(habbo.getHabboInfo().getId());
        this.response.appendInt(habbo.getHabboStats().respectPointsReceived);
        return this.response;
    }
}
