package com.habboproject.server.threads.executors.freeze;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;

import java.util.List;

/**
 * Created by brend on 04/02/2017.
 */
public class FreezeResetTileEvent implements CometThread {
    private final List<RoomItemFloor> floorItems;

    public FreezeResetTileEvent(List<RoomItemFloor> floorItems) {
        this.floorItems = floorItems;
    }

    @Override
    public void run() {
        for (RoomItemFloor floorItem : this.floorItems) {
            floorItem.setExtraData("0");
            floorItem.sendUpdate();
        }
    }
}
