package com.habboproject.server.threads.executors.wired.events;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;

/**
 * Created by brend on 29/04/2017.
 */
public class WiredItemFlashEvent extends WiredItemExecuteEvent {
    public WiredItemFlashEvent() {
        super(null, null);

        this.setTotalTicks(2);
    }

    @Override
    public void onCompletion(final RoomItemFloor floorItem) {
        if(floorItem instanceof WiredFloorItem) {
            ((WiredFloorItem) floorItem).switchState();
        }
    }

    @Override
    public boolean isInteractiveEvent() {
        return false;
    }
}
