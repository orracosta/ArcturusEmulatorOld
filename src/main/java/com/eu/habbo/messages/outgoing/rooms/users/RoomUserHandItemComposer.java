package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 12-10-2014 13:16.
 */
public class RoomUserHandItemComposer extends MessageComposer
{
    private final RoomUnit roomUnit;

    public RoomUserHandItemComposer(RoomUnit roomUnit)
    {
        this.roomUnit = roomUnit;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserHandItemComposer);
        this.response.appendInt32(this.roomUnit.getId());
        this.response.appendInt32(this.roomUnit.getHandItem());
        return this.response;
    }
}
