package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 3-11-2014 17:01.
 */
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
        this.response.appendInt32(habbo.getHabboInfo().getId());
        this.response.appendInt32(habbo.getHabboStats().respectPointsReceived);
        return this.response;
    }
}
