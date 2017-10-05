package com.habboproject.server.game.rooms.objects.items.types.floor.groups;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;

public class GroupFloorItem extends RoomItemFloor {
    public GroupFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void compose(IComposer msg, boolean isNew) {
        GroupData groupData = GroupManager.getInstance().getData(this.getGroupId());

        if (groupData == null) {
            msg.writeInt(0);
            msg.writeInt(2);
            msg.writeInt(0);
        } else {
            msg.writeInt(0);
            msg.writeInt(2);
            msg.writeInt(5);

            msg.writeString(this.getExtraData());
            msg.writeString(this.getGroupId());
            msg.writeString(groupData.getBadge());

            String colourA = GroupManager.getInstance().getGroupItems().getSymbolColours().get(groupData.getColourA()) != null ? GroupManager.getInstance().getGroupItems().getSymbolColours().get(groupData.getColourA()).getColour() : "ffffff";
            String colourB = GroupManager.getInstance().getGroupItems().getBackgroundColours().get(groupData.getColourB()) != null ? GroupManager.getInstance().getGroupItems().getBackgroundColours().get(groupData.getColourB()).getColour() : "ffffff";

            msg.writeString(colourA);
            msg.writeString(colourB);
        }
    }

    public void onEntityStepOn(RoomEntity entity, boolean instantUpdate) {
        if (!this.getDefinition().canSit()) {
            return;
        }

        double height = entity instanceof PetEntity || entity.hasAttribute("transformation") ? this.getSitHeight() / 2.0 : this.getSitHeight();

        entity.setBodyRotation(this.getRotation());
        entity.setHeadRotation(this.getRotation());

        entity.addStatus(RoomEntityStatus.SIT, String.valueOf(height).replace(',', '.'));

        if (instantUpdate) {
            this.getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(entity));
        } else {
            entity.markNeedsUpdate();
        }
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        this.onEntityStepOn(entity, false);
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (entity.hasStatus(RoomEntityStatus.SIT)) {
            entity.removeStatus(RoomEntityStatus.SIT);
        }

        entity.markNeedsUpdate();
    }

    public double getSitHeight() {
        return this.getDefinition().getHeight();
    }
}
