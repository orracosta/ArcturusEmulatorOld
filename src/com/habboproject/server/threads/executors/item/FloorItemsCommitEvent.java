package com.habboproject.server.threads.executors.item;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.habboproject.server.threads.CometThread;

import java.util.List;

/**
 * Created by brend on 07/03/2017.
 */
public class FloorItemsCommitEvent implements CometThread {
    private final List<RoomItemFloor> floorItems;

    public FloorItemsCommitEvent(List<RoomItemFloor> floorItems) {
        this.floorItems = floorItems;
    }

    @Override
    public void run() {
        for (RoomItemFloor floorItem : this.floorItems) {
            if (floorItem == null || !floorItem.needsUpdate())
                continue;

            RoomItemDao.saveItem(floorItem.getId(), floorItem.getPosition().getX(), floorItem.getPosition().getY(),
                    floorItem.getPosition().getZ(), floorItem.getRotation(), floorItem.getDataObject());

            floorItem.needsUpdate(false);
        }

        this.floorItems.clear();
    }
}
