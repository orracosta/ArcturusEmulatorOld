package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 21-9-2014 12:26.
 */
public class RemoveWallItemComposer extends MessageComposer {

    private final HabboItem item;

    public RemoveWallItemComposer(HabboItem item)
    {
        this.item = item;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RemoveWallItemComposer);
        this.response.appendString(this.item.getId() + "");
        this.response.appendInt32(this.item.getUserId());
        return this.response;
    }
}
