package com.habboproject.server.game.rooms.objects.items.types.floor.teleport;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.threads.executors.teleport.TeleportEventOne;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/02/2017.
 */
public class TeleportPadFloorItem extends TeleportDoorFloorItem {
    public TeleportPadFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
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
            return false;
        }

        entity.setOverriden(true);
        entity.getProcessingPath().clear();

        ThreadManager.getInstance().executeSchedule(new TeleportEventOne(this, (PlayerEntity)entity), 100, TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (this.getRoom() == null || entity == null || !(entity instanceof PlayerEntity) || !this.getExtraData().equals("0")) {
            return;
        }

        if (this.canInteract(entity)) {
            entity.setOverriden(true);
            entity.getProcessingPath().clear();

            ThreadManager.getInstance().executeSchedule(new TeleportEventOne(this, (PlayerEntity)entity), 100, TimeUnit.MILLISECONDS);
        }
    }

    public boolean canInteract(RoomEntity entity) {
        if (((PlayerEntity)entity).isTeleporting()) {
            return false;
        }

        if (!this.getExtraData().equals("0")) {
            return false;
        }

        return true;
    }
}
