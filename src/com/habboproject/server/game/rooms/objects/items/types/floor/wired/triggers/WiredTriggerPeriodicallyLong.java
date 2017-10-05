package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;

public class WiredTriggerPeriodicallyLong extends WiredTriggerPeriodically {
    private static final int PARAM_TICK_LENGTH = 0;

    public WiredTriggerPeriodicallyLong(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getTickCount() {
        return (this.getWiredData().getParams().get(PARAM_TICK_LENGTH)) * 10;
    }

    @Override
    public int getInterface() {
        return 12;
    }
}
