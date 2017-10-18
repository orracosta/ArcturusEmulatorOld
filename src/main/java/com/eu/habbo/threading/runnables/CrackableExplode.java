package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.AddFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;

public class CrackableExplode implements Runnable
{
    private final Room room;
    private final HabboItem habboItem;

    public CrackableExplode(Room room, HabboItem item)
    {
        this.room = room;
        this.habboItem = item;
    }

    @Override
    public void run()
    {
        if (habboItem.getRoomId() == 0)
        {
            return;
        }

        this.room.removeHabboItem(this.habboItem);
        this.room.sendComposer(new RemoveFloorItemComposer(this.habboItem).compose());
        this.habboItem.setRoomId(0);
        Emulator.getGameEnvironment().getItemManager().deleteItem(this.habboItem);
        HabboItem newItem = Emulator.getGameEnvironment().getItemManager().createItem(this.habboItem.getUserId(), Emulator.getGameEnvironment().getItemManager().getCrackableReward(this.habboItem.getBaseItem().getId()), 0, 0, "");

        if (newItem != null)
        {
            newItem.setX(this.habboItem.getX());
            newItem.setY(this.habboItem.getY());
            newItem.setZ(this.habboItem.getZ());
            this.room.addHabboItem(newItem);
            this.room.sendComposer(new AddFloorItemComposer(newItem, this.room.getFurniOwnerNames().get(newItem.getUserId())).compose());
        }
    }
}
