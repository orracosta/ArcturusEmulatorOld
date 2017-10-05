package com.habboproject.server.game.rooms.objects.items.types.floor.others;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;


public class AlertFloorItem extends RoomItemFloor {
    public AlertFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (this.ticksTimer > 0) {
            return false;
        }

        this.setExtraData("1");
        this.sendUpdate();

        this.setTicks(RoomItemFactory.getProcessTime(1.5));
        return true;
    }

    @Override
    public void onTickComplete() {
        this.setExtraData("0");
        this.sendUpdate();
    }
}
