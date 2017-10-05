package com.habboproject.server.game.rooms.objects.items.types.floor.banzai;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;

/**
 * Created by brend on 04/02/2017.
 */
public class BanzaiScoreboardFloorItem extends RoomItemFloor {
    private GameTeam gameTeam;

    public BanzaiScoreboardFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }
}
