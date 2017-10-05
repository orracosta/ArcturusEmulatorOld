package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

public class WiredTriggerPeriodically extends WiredTriggerItem {
    private static final int PARAM_TICK_LENGTH = 0;

    private final WiredItemExecuteEvent event;

    public WiredTriggerPeriodically(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        this.getWiredData().getParams().putIfAbsent(PARAM_TICK_LENGTH, 5);

        this.event = new WiredItemExecuteEvent(null, null);

        event.setTotalTicks(this.getTickCount());
        this.queueEvent(event);
    }

    public int getTickCount() {
        return this.getWiredData().getParams().get(PARAM_TICK_LENGTH);
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        this.evaluate(null, null);

        // loop
        this.event.setTotalTicks(this.getTickCount());
        this.queueEvent(this.event);
    }

    @Override
    public void onDataChange() {
        this.event.setTotalTicks(this.getTickCount());
    }

    @Override
    public int getInterface() {
        return 6;
    }
}
