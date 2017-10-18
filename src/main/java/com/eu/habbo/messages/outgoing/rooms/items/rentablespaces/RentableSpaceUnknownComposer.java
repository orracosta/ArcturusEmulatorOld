package com.eu.habbo.messages.outgoing.rooms.items.rentablespaces;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RentableSpaceUnknownComposer extends MessageComposer
{
    private int itemId;

    public RentableSpaceUnknownComposer(int itemId)
    {
        this.itemId = itemId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RentableSpaceUnknownComposer);
        this.response.appendInt(this.itemId);
        return this.response;
    }
}
