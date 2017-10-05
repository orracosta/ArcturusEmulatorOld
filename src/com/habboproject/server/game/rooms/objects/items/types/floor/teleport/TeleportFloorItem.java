package com.habboproject.server.game.rooms.objects.items.types.floor.teleport;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.executors.teleport.TeleportEventOne;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/02/2017.
 */
public class TeleportFloorItem extends RoomItemFloor {
    private long pairId = -1;

    public TeleportFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
        this.setExtraData("0");
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

        this.setExtraData("1");
        this.sendUpdate();

        ThreadManager.getInstance().executeSchedule(new TeleportEventOne(this, (PlayerEntity)entity), 500, TimeUnit.MILLISECONDS);

        return true;
    }

    public boolean canInteract(Position playerPosition, Position teleportPosition, boolean isOverride) {
        if (playerPosition.getX() != teleportPosition.getX()) {
            return false;
        }

        if (playerPosition.getY() != teleportPosition.getY()) {
            return false;
        }

        if (!playerPosition.equals(this.getPosition()) && this.getRoom().getEntities().positionHasEntity(this.getPosition().copy())) {
            return false;
        }

        if (!this.getExtraData().equals("0")) {
            return false;
        }

        return true;
    }

    public long getPairId() {
        if (this.pairId == -1) {
            this.pairId = ItemManager.getInstance().getTeleportPartner(this.getId());
        }

        return this.pairId;
    }

    public RoomItemFloor getPartner(long pairId) {
        return this.getRoom().getItems().getFloorItem(pairId);
    }

    @Override
    public void onPlaced() {
        this.setExtraData("0");
    }
}
