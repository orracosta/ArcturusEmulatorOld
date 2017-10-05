package com.habboproject.server.game.rooms.objects.items.types.floor.valentines;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityType;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.network.messages.outgoing.room.items.lovelock.LoveLockWidgetMessageComposer;

public class LoveLockFloorItem extends RoomItemFloor {
    private int leftEntity = 0;
    private int rightEntity = 0;

    public LoveLockFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void compose(IComposer msg, boolean isNew) {
        String[] loveLockData = this.getExtraData().split(String.valueOf((char) 5));

        msg.writeInt(0);
        msg.writeInt(2);

        msg.writeInt(loveLockData.length);

        for (int i = 0; i < loveLockData.length; i++) {
            msg.writeString(loveLockData[i]);
        }
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (isWiredTrigger || entity == null) {
            return false;
        }

        if (!(entity instanceof PlayerEntity)) {
            return false;
        }

        if (this.getExtraData().startsWith("1")) return false;

        Position leftPosition = null;
        Position rightPosition = null;

        switch (this.getRotation()) {
            case 2:
                leftPosition = new Position(this.getPosition().getX(), this.getPosition().getY() - 1);
                rightPosition = new Position(this.getPosition().getX(), this.getPosition().getY() + 1);
                break;

            case 4:
                leftPosition = new Position(this.getPosition().getX() + 1, this.getPosition().getY());
                rightPosition = new Position(this.getPosition().getX() - 1, this.getPosition().getY());
                break;
        }

        if (leftPosition == null) {
            return false; //right pos would also be null
        }

        final RoomTile leftTile = this.getRoom().getMapping().getTile(leftPosition);
        final RoomTile rightTile = this.getRoom().getMapping().getTile(rightPosition);

        if (leftTile == null || rightTile == null || leftTile.getEntities().size() != 1 || rightTile.getEntities().size() != 1)
            return false;

        RoomEntity leftEntity = leftTile.getEntity();
        RoomEntity rightEntity = rightTile.getEntity();

        if (leftEntity.getEntityType() != RoomEntityType.PLAYER || rightEntity.getEntityType() != RoomEntityType.PLAYER)
            return false;

        try {
            ((PlayerEntity) leftEntity).getPlayer().getSession().send(new LoveLockWidgetMessageComposer(this.getVirtualId()));
            ((PlayerEntity) rightEntity).getPlayer().getSession().send(new LoveLockWidgetMessageComposer(this.getVirtualId()));

            this.leftEntity = ((PlayerEntity) leftEntity).getPlayerId();
            this.rightEntity = ((PlayerEntity) rightEntity).getPlayerId();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public int getLeftEntity() {
        return leftEntity;
    }

    public int getRightEntity() {
        return rightEntity;
    }
}
