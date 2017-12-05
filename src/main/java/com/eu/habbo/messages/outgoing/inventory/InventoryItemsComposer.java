package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;

public class InventoryItemsComposer extends MessageComposer implements TIntObjectProcedure<HabboItem>
{
    private final int page;
    private final int out;
    private final TIntObjectMap<HabboItem> items;

    public InventoryItemsComposer(int page, int out, TIntObjectMap<HabboItem> items)
    {
        this.page = page;
        this.out = out;
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        try
        {
            this.response.init(Outgoing.InventoryItemsComposer);
            this.response.appendInt(this.out);
            this.response.appendInt(this.page - 1);
            this.response.appendInt(this.items.size());

            this.items.forEachEntry(this);
            return this.response;
        }
        catch(Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return null;
    }

    @Override
    public boolean execute(int a, HabboItem habboItem)
    {
        this.response.appendInt(habboItem.getId());
        this.response.appendString(habboItem.getBaseItem().getType().code);
        this.response.appendInt(habboItem.getId());
        this.response.appendInt(habboItem.getBaseItem().getSpriteId());

        if(habboItem.getBaseItem().getName().equals("floor") || habboItem.getBaseItem().getName().equals("landscape") || habboItem.getBaseItem().getName().equals("wallpaper") || habboItem.getBaseItem().getName().equals("poster")) {
            if (habboItem.getBaseItem().getName().equals("landscape"))
                this.response.appendInt(4);
            else if (habboItem.getBaseItem().getName().equals("floor"))
                this.response.appendInt(3);
            else if (habboItem.getBaseItem().getName().equals("wallpaper"))
                this.response.appendInt(2);
            else if (habboItem.getBaseItem().getName().equals("poster"))
                this.response.appendInt(6);


            this.response.appendInt(0);
            this.response.appendString(habboItem.getExtradata());
        }
        else
        {
            if(habboItem.getBaseItem().getName().equals("gnome_box"))
                this.response.appendInt(13);
            else
                this.response.appendInt(habboItem instanceof InteractionGift ? ((((InteractionGift) habboItem).getColorId() * 1000) + ((InteractionGift) habboItem).getRibbonId()) : 1);

            habboItem.serializeExtradata(this.response);
        }
        this.response.appendBoolean(habboItem.getBaseItem().allowRecyle());
        this.response.appendBoolean(habboItem.getBaseItem().allowTrade());
        this.response.appendBoolean(!habboItem.isLimited() && habboItem.getBaseItem().allowInventoryStack() && !(habboItem instanceof InteractionGift));
        this.response.appendBoolean(habboItem.getBaseItem().allowMarketplace());
        this.response.appendInt(-1);
        this.response.appendBoolean(true);
        this.response.appendInt(-1);

        if (habboItem.getBaseItem().getType() == FurnitureType.FLOOR ) {
            this.response.appendString("");
            this.response.appendInt(habboItem instanceof InteractionGift ? ((((InteractionGift) habboItem).getColorId() * 1000) + ((InteractionGift) habboItem).getRibbonId()) : 1);
        }

        return true;
    }

}
