package com.habboproject.server.game.rooms.objects.items.types.floor.teleport;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.threads.executors.teleport.TeleportEventOne;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/02/2017.
 */
public class TeleportDoorFloorItem extends TeleportFloorItem {
    public TeleportDoorFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (this.getRoom() == null || entity == null || !(entity instanceof PlayerEntity) || isWiredTrigger) {
            return false;
        }

        Position posInFront = this.getPosition().squareInFront(this.getRotation());
        if (!this.canInteract(entity.getPosition().copy(), this.getPosition().copy(), entity.isOverriden()) && !this.canInteract(entity.getPosition().copy(), posInFront, entity.isOverriden())) {
            entity.moveTo(posInFront.getX(), posInFront.getY());

            RoomTile inFrontTile = this.getRoom().getMapping().getTile(posInFront.getX(), posInFront.getY());
            if (inFrontTile != null) {
                inFrontTile.scheduleEvent(entity.getId(), e -> {
                            this.onInteract(e, requestData, false);
                        }
                );
            }

            return false;
        }

        entity.setOverriden(true);

        ThreadManager.getInstance().executeSchedule(new TeleportEventOne(this, (PlayerEntity)entity), 500, TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public boolean canInteract(Position playerPosition, Position teleportPosition, boolean isOverride) {
        if (playerPosition.getX() != teleportPosition.getX()) {
            return false;
        }

        if (playerPosition.getY() != teleportPosition.getY()) {
            return false;
        }

        if (!this.getExtraData().equals("0")) {
            return false;
        }

        return true;
    }
}
