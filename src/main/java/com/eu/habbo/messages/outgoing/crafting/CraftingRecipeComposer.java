package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CraftingRecipeComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CraftingRecipeComposer);

        synchronized (Emulator.getGameEnvironment().getCraftingManager())
        {
            this.response.appendInt32(Emulator.getGameEnvironment().getCraftingManager().getRecipes().size()); //Count

            for(CraftingRecipe recipe : Emulator.getGameEnvironment().getCraftingManager().getRecipes())
            {
                this.response.appendInt32(recipe.ingredients.size()); //Count
                this.response.appendString(recipe.result);
            }

        }
        return this.response;
    }
}
