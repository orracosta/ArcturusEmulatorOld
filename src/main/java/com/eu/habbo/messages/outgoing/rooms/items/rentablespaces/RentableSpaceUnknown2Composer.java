package com.eu.habbo.messages.outgoing.rooms.items.rentablespaces;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RentableSpaceUnknown2Composer extends MessageComposer
{
    private int itemId;

    public RentableSpaceUnknown2Composer(int itemId)
    {
        this.itemId = itemId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RentableSpaceUnknown2Composer);
        this.response.appendInt(this.itemId);
        return this.response;
    }
}
