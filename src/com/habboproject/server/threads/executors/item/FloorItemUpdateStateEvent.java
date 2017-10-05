package com.habboproject.server.threads.executors.item;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 11/03/2017.
 */
public class FloorItemUpdateStateEvent implements CometThread {
    private final RoomItemFloor floorItem;
    private final String state;

    public FloorItemUpdateStateEvent(RoomItemFloor floorItem, String state) {
        this.floorItem = floorItem;
        this.state = state;
    }

    @Override
    public void run() {
        floorItem.setExtraData(state);
        floorItem.sendUpdate();
    }
}
