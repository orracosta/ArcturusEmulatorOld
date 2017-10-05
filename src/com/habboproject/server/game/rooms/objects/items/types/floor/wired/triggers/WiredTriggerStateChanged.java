package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;


public class WiredTriggerStateChanged extends WiredTriggerItem {
    public WiredTriggerStateChanged(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 1;
    }

    public static boolean executeTriggers(RoomEntity entity, RoomItemFloor floorItem) {
        boolean wasExecuted = false;

        for (RoomItemFloor wiredItem : entity.getRoom().getItems().getByClass(WiredTriggerStateChanged.class)) {
            WiredTriggerStateChanged trigger = ((WiredTriggerStateChanged) wiredItem);

            if (trigger.getWiredData().getSelectedIds().contains(floorItem.getId()))
                wasExecuted = trigger.evaluate(entity, floorItem);
        }

        return wasExecuted;
    }
}
