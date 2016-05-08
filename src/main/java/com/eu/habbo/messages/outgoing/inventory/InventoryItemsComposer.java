package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.procedure.TIntObjectProcedure;

public class InventoryItemsComposer extends MessageComposer implements TIntObjectProcedure<HabboItem>
{

    private Habbo habbo;

    public InventoryItemsComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        try
        {
            this.response.init(Outgoing.InventoryItemsComposer);
            this.response.appendInt32(1);
            this.response.appendInt32(0);
            this.response.appendInt32(this.habbo.getHabboInventory().getItemsComponent().getItems().size());

            this.habbo.getHabboInventory().getItemsComponent().getItems().forEachEntry(this);
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
        this.response.appendInt32(habboItem.getId());
        this.response.appendString(habboItem.getBaseItem().getType().toUpperCase());
        this.response.appendInt32(habboItem.getId());
        this.response.appendInt32(habboItem.getBaseItem().getSpriteId());

        if(habboItem.getBaseItem().getName().equals("floor") || habboItem.getBaseItem().getName().equals("landscape") || habboItem.getBaseItem().getName().equals("wallpaper") || habboItem.getBaseItem().getName().equals("poster")) {
            if (habboItem.getBaseItem().getName().equals("landscape"))
                this.response.appendInt32(4);
            else if (habboItem.getBaseItem().getName().equals("floor"))
                this.response.appendInt32(3);
            else if (habboItem.getBaseItem().getName().equals("wallpaper"))
                this.response.appendInt32(2);
            else if (habboItem.getBaseItem().getName().equals("poster"))
                this.response.appendInt32(6);


            this.response.appendInt32(0);
            this.response.appendString(habboItem.getExtradata());
        }
        else
        {
            if(habboItem.getBaseItem().getName().equals("gnome_box"))
                this.response.appendInt32(13);
            else
                this.response.appendInt32(habboItem instanceof InteractionGift ? ((((InteractionGift) habboItem).getColorId() * 1000) + ((InteractionGift) habboItem).getRibbonId()) : 1);

            habboItem.serializeExtradata(this.response);
        }
        this.response.appendBoolean(habboItem.getBaseItem().allowRecyle());
        this.response.appendBoolean(habboItem.getBaseItem().allowTrade());
        this.response.appendBoolean(!habboItem.isLimited() && habboItem.getBaseItem().allowInventoryStack() && !(habboItem instanceof InteractionGift));
        this.response.appendBoolean(habboItem.getBaseItem().allowMarketplace());
        this.response.appendInt32(-1);
        this.response.appendBoolean(true);
        this.response.appendInt32(-1);

        if (!habboItem.getBaseItem().getType().equals("i")) {
            this.response.appendString("");
            this.response.appendInt32(habboItem instanceof InteractionGift ? ((((InteractionGift) habboItem).getColorId() * 1000) + ((InteractionGift) habboItem).getRibbonId()) : 1);
        }

        return true;
    }

}
