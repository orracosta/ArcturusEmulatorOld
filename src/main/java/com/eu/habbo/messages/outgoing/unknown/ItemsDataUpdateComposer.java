package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class ItemsDataUpdateComposer extends MessageComposer
{
    private final List<HabboItem> items;

    public ItemsDataUpdateComposer(List<HabboItem> items)
    {
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ItemsDataUpdateComposer);
        this.response.appendInt(this.items.size());
        for (HabboItem item : this.items)
        {
            item.serializeExtradata(this.response);
        }
        return this.response;
    }
}