package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 10-1-2016 15:50.
 */
public class CraftableProductsComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CraftableProductsComposer);

        synchronized (Emulator.getGameEnvironment().getCraftingManager().getRecipes())
        {
            this.response.appendInt32(1); //Count
            this.response.appendString("a");
            this.response.appendString("b");

            this.response.appendInt32(Emulator.getGameEnvironment().getCraftingManager().getRecipes().size()); //Count

            for(CraftingRecipe recipe : Emulator.getGameEnvironment().getCraftingManager().getRecipes())
            {
                this.response.appendString(recipe.result);
            }
        }
        return this.response;
    }
}
