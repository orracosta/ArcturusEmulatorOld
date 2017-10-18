package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.items.PresentItemOpenedComposer;

public class OpenGift implements Runnable
{
    private final HabboItem item;
    private final Habbo habbo;
    private final Room room;

    public OpenGift(HabboItem item, Habbo habbo, Room room)
    {
        this.item = item;
        this.habbo = habbo;
        this.room = room;
    }

    @Override
    public void run()
    {
        try
        {
            HabboItem inside = null;

            for (HabboItem i : ((InteractionGift) item).items)
            {
                if(inside == null)
                    inside = i;

                i.setUserId(this.habbo.getHabboInfo().getId());
                i.needsUpdate(true);
                i.run();
            }

            this.habbo.getInventory().getItemsComponent().addItems(((InteractionGift) this.item).items);

            RoomTile tile = this.room.getLayout().getTile(this.item.getX(), this.item.getY());
            if (tile != null)
            {
                this.room.updateTile(tile);
            }
            this.habbo.getClient().sendResponse(new AddHabboItemComposer(((InteractionGift) this.item).items));
            this.habbo.getClient().sendResponse(new InventoryRefreshComposer());


            Emulator.getThreading().run(new QueryDeleteHabboItem(this.item));
            Emulator.getThreading().run(new RemoveFloorItemTask(this.room, this.item), item.getBaseItem().getName().contains("present_wrap") ? 5000 : 0);

            if (inside != null)
            {
                this.habbo.getClient().sendResponse(new PresentItemOpenedComposer(inside, "", false));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
