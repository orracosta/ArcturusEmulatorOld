package com.eu.habbo.messages.incoming.catalog.recycler;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.RecyclerCompleteComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import gnu.trove.set.hash.THashSet;

public class RecycleEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(Emulator.getGameEnvironment().getCatalogManager().ecotronItem != null)
        {
            THashSet<HabboItem> items = new THashSet<HabboItem>();

            int count = this.packet.readInt();

            for (int i = 0; i < count; i++)
            {
                HabboItem item = this.client.getHabbo().getHabboInventory().getItemsComponent().getHabboItem(this.packet.readInt());

                if (item == null)
                    return;

                if (item.getBaseItem().allowRecyle())
                {
                    items.add(item);
                }
            }

            if (items.size() == count)
            {
                for (HabboItem item : items)
                {
                    this.client.getHabbo().getHabboInventory().getItemsComponent().removeHabboItem(item);
                    this.client.sendResponse(new RemoveHabboItemComposer(item.getId()));
                    Emulator.getThreading().run(new QueryDeleteHabboItem(item));
                }
            }

            HabboItem reward = Emulator.getGameEnvironment().getItemManager().handleRecycle(this.client.getHabbo(), Emulator.getGameEnvironment().getCatalogManager().getRandomRecyclerPrize().getId() + "");
            if(reward == null)
                return;

            this.client.sendResponse(new AddHabboItemComposer(reward));
            this.client.getHabbo().getHabboInventory().getItemsComponent().addItem(reward);
            this.client.sendResponse(new RecyclerCompleteComposer(RecyclerCompleteComposer.RECYCLING_COMPLETE));
            this.client.sendResponse(new InventoryRefreshComposer());

            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("FurnimaticQuest"));
        }
        else
        {
            this.client.sendResponse(new RecyclerCompleteComposer(RecyclerCompleteComposer.RECYCLING_CLOSED));
        }
    }
}
