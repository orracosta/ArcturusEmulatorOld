package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 20-9-2014 20:02.
 */
public class AddWallItemComposer extends MessageComposer {

    private final HabboItem item;

    public AddWallItemComposer(HabboItem item)
    {
        this.item = item;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.AddWallItemComposer);
        this.item.serializeWallData(this.response);
        this.response.appendInt32(this.item.getUserId());
        this.response.appendString("Owner");
        return this.response;
    }
}
