package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 21-9-2014 13:06.
 */
public class InventoryUpdateItemComposer extends MessageComposer {

    private final HabboItem habboItem;

    public InventoryUpdateItemComposer(HabboItem item)
    {
        this.habboItem = item;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.InventoryItemUpdateComposer);
        this.response.appendInt32(2);
        this.response.appendInt32(habboItem.getId());
        this.response.appendString(habboItem.getBaseItem().getType().toUpperCase());
        this.response.appendInt32(habboItem.getId());
        this.response.appendInt32(habboItem.getBaseItem().getSpriteId());

        if (habboItem.getBaseItem().getName().equals("landscape"))
            this.response.appendInt32(4);
        else if (habboItem.getBaseItem().getName().equals("floor"))
            this.response.appendInt32(3);
        else if (habboItem.getBaseItem().getName().equals("wallpaper"))
            this.response.appendInt32(2);

        if(habboItem.isLimited())
        {
            this.response.appendInt32(1);
            this.response.appendInt32(256);
            this.response.appendString(habboItem.getExtradata());
            this.response.appendInt32(habboItem.getLimitedSells());
            this.response.appendInt32(habboItem.getLimitedStack());
        }
        else
        {
            this.response.appendInt32(1);
            this.response.appendInt32(0);
            this.response.appendString(habboItem.getExtradata());
        }
        this.response.appendBoolean(habboItem.getBaseItem().allowRecyle());
        this.response.appendBoolean(habboItem.getBaseItem().allowTrade());
        this.response.appendBoolean(!habboItem.isLimited() && habboItem.getBaseItem().allowInventoryStack());
        this.response.appendBoolean(habboItem.getBaseItem().allowMarketplace());
        this.response.appendInt32(-1);
        this.response.appendBoolean(false);
        this.response.appendInt32(-1);

        if (!habboItem.getBaseItem().getType().equals("i")) {
            this.response.appendString("");
            this.response.appendInt32(0);
        }
        this.response.appendInt32(100);
        return this.response;
    }
}
