package com.habboproject.server.game.rooms.objects.items.types.floor.snowboarding;

import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.AdjustableHeightFloorItem;
import com.habboproject.server.game.rooms.types.Room;


public class SnowboardSlopeFloorItem extends AdjustableHeightFloorItem {
    public SnowboardSlopeFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }
}
