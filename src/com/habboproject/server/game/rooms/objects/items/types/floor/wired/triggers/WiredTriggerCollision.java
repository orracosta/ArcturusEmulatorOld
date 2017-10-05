package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;

public class WiredTriggerCollision extends WiredTriggerItem {
    public WiredTriggerCollision(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 9;
    }

    public static boolean executeTriggers(RoomEntity entity) {
        boolean wasExecuted = false;

        for (RoomItemFloor floorItem : entity.getRoom().getItems().getByClass(WiredTriggerCollision.class)) {
            WiredTriggerCollision trigger = ((WiredTriggerCollision) floorItem);

            wasExecuted = trigger.evaluate(entity, null);
        }

        return wasExecuted;
    }
}
