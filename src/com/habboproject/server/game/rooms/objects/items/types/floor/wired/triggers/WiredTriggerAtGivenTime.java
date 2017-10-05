package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;


public class WiredTriggerAtGivenTime extends WiredTriggerItem {
    private static final int PARAM_TICK_LENGTH = 0;

    private boolean needsReset = false;

    public WiredTriggerAtGivenTime(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getWiredData().getParams().get(PARAM_TICK_LENGTH) == null) {
            this.getWiredData().getParams().put(PARAM_TICK_LENGTH, 2); // 1s
        }
    }

    @Override
    public boolean suppliesPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 6;
    }

    public int getTime() {
        return this.getWiredData().getParams().get(PARAM_TICK_LENGTH);
    }

    public static boolean executeTriggers(Room room, int timer) {
        boolean wasExecuted = false;

        for (RoomItemFloor wiredItem : room.getItems().getByClass(WiredTriggerAtGivenTime.class)) {
            WiredTriggerAtGivenTime trigger = ((WiredTriggerAtGivenTime) wiredItem);

            if (timer >= trigger.getTime() && !trigger.needsReset) {
                if (trigger.evaluate(null, null)) {
                    wasExecuted = true;

                    trigger.needsReset = true;
                }
            }
        }

        return wasExecuted;
    }

    public void setNeedsReset(boolean needsReset) {
        this.needsReset = needsReset;
    }

    public boolean needsReset() {
        return this.needsReset;
    }
}
