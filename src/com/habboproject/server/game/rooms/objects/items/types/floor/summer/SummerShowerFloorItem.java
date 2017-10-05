package com.habboproject.server.game.rooms.objects.items.types.floor.summer;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;


public class SummerShowerFloorItem extends RoomItemFloor {
    public SummerShowerFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        this.setExtraData("1");
        this.sendUpdate();
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        this.setExtraData("0");
        this.sendUpdate();
    }
}
