package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.negative;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.WiredConditionActorInTeam;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 03/02/2017.
 */
public class WiredNegativeConditionActorInTeam extends WiredConditionActorInTeam {
    public WiredNegativeConditionActorInTeam(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
        this.isNegative = true;
    }
}
