package com.habboproject.server.game.rooms.objects.items.types.floor.groups;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.executors.gate.GateCloseEvent;
import com.habboproject.server.threads.executors.gate.GateOpenEvent;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

public class GroupGateFloorItem extends RoomItemFloor {
    public GroupGateFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
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

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (entity instanceof PlayerEntity && (((PlayerEntity)entity).getPlayer().getGroups().contains(this.getGroupId()) || ((PlayerEntity)entity).getPlayer().getPermissions().getRank().roomFullControl())) {
            for (AffectedTile tile : AffectedTile.getAffectedTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
                if (this.getRoom().getEntities().getEntitiesAt(new Position(tile.x, tile.y)).size() <= 0)
                    continue;

                return false;
            }

            if (this.getRoom().getEntities().getEntitiesAt(this.getPosition()).size() > 0) {
                return false;
            }

            for (RoomEntity entity0 : this.getRoom().getEntities().getAllEntities().values()) {
                if (this.getPosition().distanceTo(entity0.getPosition()) > 1.0 || !entity0.isWalking())
                    continue;

                return false;
            }

            if (this.getExtraData() == "0") {
                this.setExtraData("1");
            } else {
                this.setExtraData("0");
            }

            this.sendUpdate();

            return true;
        }

        return false;
    }

    @Override
    public void onEntityPreStepOn(RoomEntity entity) {
        ThreadManager.getInstance().execute(new GateOpenEvent(this));
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        ThreadManager.getInstance().executeSchedule(new GateCloseEvent(this), 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isMovementCancelled(RoomEntity entity) {
        if (entity == null)
            return true;

        if (!(entity instanceof PlayerEntity)) {
            return true;
        }

        if (!((PlayerEntity)entity).getPlayer().getGroups().contains(this.getGroupId())) {
            return true;
        }

        return false;
    }

    public boolean isOpen() {
        return !this.getExtraData().equals("0");
    }
}
