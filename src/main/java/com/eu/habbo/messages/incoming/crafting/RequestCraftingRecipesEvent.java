package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.crafting.CraftingRecipeComposer;

/**
 * Created on 10-1-2016 16:25.
 */
public class RequestCraftingRecipesEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        System.out.println("Crafting: " + this.getClass().getName());
        int itemId = this.packet.readInt();

        HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

        if(item != null)
        {
            item.setExtradata("2");
            this.client.getHabbo().getHabboInfo().getCurrentRoom().updateItem(item);
            this.client.sendResponse(new CraftingRecipeComposer());
        }
    }
}
