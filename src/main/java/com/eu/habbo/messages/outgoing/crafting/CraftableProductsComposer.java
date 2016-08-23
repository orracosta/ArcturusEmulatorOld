package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CraftableProductsComposer extends MessageComposer
{
    private final List<CraftingRecipe> recipes;
    private final Collection<Item> ingredients;

    public CraftableProductsComposer(List<CraftingRecipe> recipes, Collection<Item> ingredients)
    {
        this.recipes = recipes;
        this.ingredients = ingredients;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CraftableProductsComposer);

        this.response.appendInt32(this.recipes.size());
        for (CraftingRecipe recipe : recipes)
        {
            this.response.appendString(recipe.getName());
            this.response.appendString(recipe.getReward().getName());
        }

        this.response.appendInt32(this.ingredients.size());
        for (Item item : ingredients)
        {
            this.response.appendString(item.getName());
        }

        return this.response;
    }
}
