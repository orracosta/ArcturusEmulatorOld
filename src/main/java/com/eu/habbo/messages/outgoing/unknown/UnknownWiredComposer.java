package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownWiredComposer extends MessageComposer
{
    private final HabboItem item;

    public UnknownWiredComposer(HabboItem item)
    {
        this.item = item;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownWiredComposer);
        this.response.appendInt(this.item.getId());
        return this.response;
    }
}