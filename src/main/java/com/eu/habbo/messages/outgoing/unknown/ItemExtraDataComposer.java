package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ItemExtraDataComposer extends MessageComposer
{
    private final HabboItem item;

    public ItemExtraDataComposer(HabboItem item)
    {
        this.item = item;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ItemExtraDataComposer);
        this.response.appendString(this.item.getId() + "");
        this.item.serializeExtradata(this.response);
        return this.response;
    }
}