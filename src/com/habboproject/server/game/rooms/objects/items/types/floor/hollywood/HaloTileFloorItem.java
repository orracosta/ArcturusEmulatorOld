package com.habboproject.server.game.rooms.objects.items.types.floor.hollywood;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;


public class HaloTileFloorItem extends RoomItemFloor {
    public HaloTileFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
        this.setExtraData("0");
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        this.setExtraData("1");
        this.sendUpdate();
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (this.ticksTimer < 1) {
            this.setTicks(RoomItemFactory.getProcessTime(0.5));
        }
    }

    @Override
    public void onTickComplete() {
        this.setExtraData("0");
        this.sendUpdate();
    }
}
