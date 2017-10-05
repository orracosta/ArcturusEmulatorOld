package com.habboproject.server.game.rooms.objects.items.types;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.types.Room;


public final class DefaultWallItem extends RoomItemWall {
    public DefaultWallItem(long id, int itemId, Room room, int owner, String position, String data) {
        super(id, itemId, room, owner, position, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!(entity instanceof PlayerEntity))
                return false;

            if (!entity.getRoom().getRights().hasRights(((PlayerEntity) entity).getPlayerId())) {
                return false;
            }
        }

        this.toggleInteract(true);
        this.sendUpdate();

        this.saveData();
        return true;
    }
}
