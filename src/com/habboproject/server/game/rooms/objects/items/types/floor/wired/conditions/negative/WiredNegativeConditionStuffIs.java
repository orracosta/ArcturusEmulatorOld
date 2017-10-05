package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.negative;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.WiredConditionStuffIs;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 03/02/2017.
 */
public class WiredNegativeConditionStuffIs extends WiredConditionStuffIs {
    public WiredNegativeConditionStuffIs(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
        this.isNegative = true;
    }
}
