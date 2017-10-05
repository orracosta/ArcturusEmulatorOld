package com.habboproject.server.game.rooms.objects.items.types.floor.football;

import com.habboproject.server.game.rooms.objects.items.types.floor.rollable.RollableFloorItem;
import com.habboproject.server.game.rooms.types.Room;


public class FootballFloorItem extends RollableFloorItem {
    public FootballFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }
}
