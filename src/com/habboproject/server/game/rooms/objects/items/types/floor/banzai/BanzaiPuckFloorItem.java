package com.habboproject.server.game.rooms.objects.items.types.floor.banzai;

import com.habboproject.server.game.rooms.objects.items.types.floor.rollable.RollableFloorItem;
import com.habboproject.server.game.rooms.types.Room;


public class BanzaiPuckFloorItem extends RollableFloorItem {
    public BanzaiPuckFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }
}
