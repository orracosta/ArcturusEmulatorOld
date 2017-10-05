package com.habboproject.server.threads.executors.gate;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 04/02/2017.
 */
public class GateCloseEvent implements CometThread {
    private final RoomItemFloor floorItem;

    public GateCloseEvent(RoomItemFloor floorItem) {
        this.floorItem = floorItem;
    }

    @Override
    public void run() {
        if (!this.floorItem.getExtraData().equals("0") && this.floorItem.getRoom().getEntities().getEntitiesAt(this.floorItem.getPosition().copy()).isEmpty()) {
            this.floorItem.setExtraData("0");
            this.floorItem.sendUpdate();
        }
    }
}
