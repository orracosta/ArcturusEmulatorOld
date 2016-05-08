package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 23-1-2015 12:27.
 */
public class PresentItemOpenedComposer extends MessageComposer
{
    private final HabboItem item;
    private final String text;
    private final boolean unknown;

    public PresentItemOpenedComposer(HabboItem item, String text, boolean unknown)
    {
        this.item = item;
        this.text = text;
        this.unknown = unknown;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PresentItemOpenedComposer);
        this.response.appendString(this.item.getBaseItem().getType());
        this.response.appendInt32(this.item.getBaseItem().getSpriteId());
        this.response.appendString(this.item.getBaseItem().getName());
        this.response.appendInt32(this.item.getId());
        this.response.appendString(this.item.getBaseItem().getType());
        this.response.appendBoolean(this.unknown);
        this.response.appendString(this.text);
        return this.response;
    }
}
