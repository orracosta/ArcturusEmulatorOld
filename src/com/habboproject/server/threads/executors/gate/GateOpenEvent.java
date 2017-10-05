package com.habboproject.server.threads.executors.gate;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 04/02/2017.
 */
public class GateOpenEvent implements CometThread {
    private final RoomItemFloor floorItem;

    public GateOpenEvent(RoomItemFloor floorItem) {
        this.floorItem = floorItem;
    }

    @Override
    public void run() {
        this.floorItem.setExtraData("1");
        this.floorItem.sendUpdate();
    }
}
