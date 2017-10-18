package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

public class BanzaiRandomTeleport implements Runnable
{
    private HabboItem item;
    private HabboItem toItem;
    private Habbo habbo;
    private Room room;

    public BanzaiRandomTeleport(HabboItem item, HabboItem toItem, Habbo habbo, Room room)
    {
        this.item = item;
        this.toItem = toItem;
        this.habbo = habbo;
        this.room = room;
    }

    @Override
    public void run()
    {
        this.habbo.getRoomUnit().setCanWalk(true);
        this.item.setExtradata("0");
        this.toItem.setExtradata("0");
        this.room.updateItem(this.item);
        this.room.updateItem(this.toItem);
        this.room.teleportHabboToItem(this.habbo, this.toItem);
    }
}
