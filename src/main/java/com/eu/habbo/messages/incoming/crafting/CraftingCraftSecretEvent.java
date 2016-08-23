package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.crafting.CraftingAltar;
import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.crafting.CraftingResultComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItems;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;
import java.util.Set;

public class CraftingCraftSecretEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        System.out.println(this.getClass().getName());
        int altarId = this.packet.readInt();
        int count = this.packet.readInt();

        HabboItem craftingAltar = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(altarId);

        if (craftingAltar != null)
        {
            CraftingAltar altar = Emulator.getGameEnvironment().getCraftingManager().getAltar(craftingAltar.getBaseItem());

            if (altar != null)
            {
                Set<HabboItem> habboItems = new THashSet<HabboItem>();
                Map<Item, Integer> items = new THashMap<Item, Integer>();

                for (int i = 0; i < count; i++)
                {
                    HabboItem habboItem = this.client.getHabbo().getHabboInventory().getItemsComponent().getHabboItem(this.packet.readInt());

                    if (habboItem == null)
                    {
                        this.client.sendResponse(new CraftingResultComposer(null));
                        return;
                    }

                    habboItems.add(habboItem);

                    if (!items.containsKey(habboItem.getBaseItem()))
                    {
                        items.put(habboItem.getBaseItem(), 0);
                    }

                    items.put(habboItem.getBaseItem(), items.get(habboItem.getBaseItem()) + 1);
                }

                CraftingRecipe recipe = altar.getRecipe(items);

                if (recipe != null)
                {
                    HabboItem rewardItem = Emulator.getGameEnvironment().getItemManager().createItem(this.client.getHabbo().getHabboInfo().getId(), recipe.getReward(), 0, 0, "");

                    if (rewardItem != null)
                    {
                        this.client.sendResponse(new CraftingResultComposer(recipe));
                        this.client.getHabbo().getHabboStats().addRecipe(recipe.getId());
                        this.client.getHabbo().getHabboInventory().getItemsComponent().addItem(rewardItem);
                        this.client.sendResponse(new AddHabboItemComposer(rewardItem));
                        for (HabboItem item : habboItems)
                        {
                            this.client.getHabbo().getHabboInventory().getItemsComponent().removeHabboItem(item);
                            this.client.sendResponse(new RemoveHabboItemComposer(item.getId()));
                            Emulator.getThreading().run(new QueryDeleteHabboItem(item));
                        }
                        this.client.sendResponse(new InventoryRefreshComposer());
                        return;
                    }
                }
            }
        }

        this.client.sendResponse(new CraftingResultComposer(null));
    }
}
