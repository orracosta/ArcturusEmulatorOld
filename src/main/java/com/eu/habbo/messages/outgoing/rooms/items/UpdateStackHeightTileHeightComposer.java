package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UpdateStackHeightTileHeightComposer extends MessageComposer
{
    private final HabboItem item;
    private final int height;

    public UpdateStackHeightTileHeightComposer(HabboItem item, double height)
    {
        this.item = item;

        if(height * 100 < 999)
            this.height = (int) height * 100;
        else
            this.height = 999;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UpdateStackHeightTileHeightComposer);
        this.response.appendInt(this.item.getId());
        this.response.appendInt(this.height);
        return this.response;
    }
}
