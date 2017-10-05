package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.negative;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.WiredConditionMatchSnapshot;
import com.habboproject.server.game.rooms.types.Room;


public class WiredNegativeConditionMatchSnapshot extends WiredConditionMatchSnapshot {
    public WiredNegativeConditionMatchSnapshot(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
        this.isNegative = true;
    }
}
