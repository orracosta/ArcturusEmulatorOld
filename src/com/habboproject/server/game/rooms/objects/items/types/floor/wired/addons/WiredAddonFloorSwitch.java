package com.habboproject.server.game.rooms.objects.items.types.floor.wired.addons;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;


public class WiredAddonFloorSwitch extends RoomItemFloor {
    public WiredAddonFloorSwitch(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (entity != null) {
            if (!this.getPosition().touching(entity)) {
                entity.moveTo(this.getPosition().squareBehind(this.getRotation()).getX(), this.getPosition().squareBehind(this.getRotation()).getY());
                return false;
            }
        }

        this.toggleInteract(true);

        this.sendUpdate();
        this.saveData();

        return true;
    }
}
