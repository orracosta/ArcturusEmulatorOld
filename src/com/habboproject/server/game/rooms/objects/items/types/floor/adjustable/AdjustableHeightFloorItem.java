package com.habboproject.server.game.rooms.objects.items.types.floor.adjustable;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import org.apache.commons.lang.StringUtils;


public class AdjustableHeightFloorItem extends RoomItemFloor {
    public AdjustableHeightFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            PlayerEntity pEntity = (PlayerEntity)entity;
            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId()) && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return false;
            }
        }

        for (RoomItemFloor floorItem : this.getItemsOnStack()) {
            if (floorItem.getId() == this.getId() || floorItem.getPosition().getZ() < this.getPosition().getZ())
                continue;

            return false;
        }

        this.toggleInteract(true);

        this.sendUpdate();

        for (RoomEntity entityOnItem : this.getEntitiesOnItem()) {
            if (entityOnItem.hasStatus(RoomEntityStatus.SIT)) {
                entityOnItem.removeStatus(RoomEntityStatus.SIT);
            }

            entityOnItem.setPosition(new Position(entityOnItem.getPosition().getX(), entityOnItem.getPosition().getY(), this.getTotalHeight()));

            this.getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(entityOnItem));
        }

        this.saveData();

        return true;
    }

    public double getCurrentHeight() {
        if (!this.getExtraData().isEmpty() && this.getExtraData().equals("0")) {
            return this.getDefinition().getHeight();
        }

        if (this.getDefinition().getVariableHeights() != null && !this.getExtraData().isEmpty()) {
            if (!StringUtils.isNumeric(this.getExtraData())) {
                return 0.0;
            }

            int heightIndex = Integer.parseInt(this.getExtraData());
            if (heightIndex >= this.getDefinition().getVariableHeights().length) {
                return 0.0;
            }

            return this.getDefinition().getVariableHeights()[heightIndex];
        }

        if (this.getDefinition().getVariableHeights() != null && this.getDefinition().getVariableHeights().length != 0) {
            return this.getDefinition().getVariableHeights()[0];
        }

        if (this.getExtraData().isEmpty() || !StringUtils.isNumeric(this.getExtraData())) {
            return this.getDefinition().getHeight();
        }

        return this.getDefinition().getHeight();
    }
}
