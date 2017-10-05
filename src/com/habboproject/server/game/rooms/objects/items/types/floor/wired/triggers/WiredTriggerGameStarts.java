package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;


public class WiredTriggerGameStarts extends WiredTriggerItem {
    public WiredTriggerGameStarts(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean suppliesPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 9;
    }

    public static boolean executeTriggers(Room room) {
        boolean wasExecuted = false;

        for (RoomItemFloor floorItem : room.getItems().getByClass(WiredTriggerGameStarts.class)) {
            WiredTriggerGameStarts trigger = ((WiredTriggerGameStarts) floorItem);

            wasExecuted = trigger.evaluate(null, null);
        }

        return wasExecuted;
    }
}
