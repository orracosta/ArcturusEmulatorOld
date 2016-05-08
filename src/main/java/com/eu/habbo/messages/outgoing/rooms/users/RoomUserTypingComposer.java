package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 6-10-2014 20:32.
 */
public class RoomUserTypingComposer extends MessageComposer
{
    private final RoomUnit roomUnit;
    private final boolean typing;

    public RoomUserTypingComposer(RoomUnit roomUnit, boolean typing)
    {
        this.roomUnit = roomUnit;
        this.typing = typing;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserTypingComposer);
        this.response.appendInt32(this.roomUnit.getId());
        this.response.appendInt32(this.typing ? 1 : 0);
        return this.response;
    }
}
