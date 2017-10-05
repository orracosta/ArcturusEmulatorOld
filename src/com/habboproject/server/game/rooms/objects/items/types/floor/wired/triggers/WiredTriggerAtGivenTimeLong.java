package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;

public class WiredTriggerAtGivenTimeLong extends WiredTriggerAtGivenTime {
    private static final int PARAM_TICK_LENGTH = 0;

    public WiredTriggerAtGivenTimeLong(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getWiredData().getParams().get(PARAM_TICK_LENGTH) == null) {
            this.getWiredData().getParams().put(PARAM_TICK_LENGTH, 2); // 1s
        }
    }

    @Override
    public int getTime() {
        return this.getWiredData().getParams().get(PARAM_TICK_LENGTH) * 10;
    }

    public static boolean executeTriggers(Room room, int timer) {
        boolean wasExecuted = false;

        for (RoomItemFloor wiredItem : room.getItems().getByClass(WiredTriggerAtGivenTime.class)) {
            WiredTriggerAtGivenTimeLong trigger = ((WiredTriggerAtGivenTimeLong) wiredItem);

            if (timer >= trigger.getTime()) {
                wasExecuted = trigger.evaluate(null, null);
            }
        }

        return wasExecuted;
    }

    @Override
    public int getInterface() {
        return 12;
    }
}
