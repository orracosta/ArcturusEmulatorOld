package com.habboproject.server.threads.executors.engine;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 04/02/2017.
 */
public class ItemUpdateEvent implements CometThread {
    private final RoomItemFloor floorItem;
    private final String state;

    public ItemUpdateEvent(RoomItemFloor floorItem, String state) {
        this.floorItem = floorItem;
        this.state = state;
    }

    @Override
    public void run() {
        if (this.floorItem == null || this.state.isEmpty()) {
            return;
        }

        this.floorItem.setExtraData(this.state);
        this.floorItem.sendUpdate();
    }
}
