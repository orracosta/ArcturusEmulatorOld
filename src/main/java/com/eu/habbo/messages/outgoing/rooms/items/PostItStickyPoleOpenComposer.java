package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PostItStickyPoleOpenComposer extends MessageComposer
{
    private final HabboItem item;

    public PostItStickyPoleOpenComposer(HabboItem item)
    {
        this.item = item;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PostItStickyPoleOpenComposer);
        this.response.appendInt(this.item == null ? -1234 : this.item.getId());
        this.response.appendString("");
        return this.response;
    }
}
