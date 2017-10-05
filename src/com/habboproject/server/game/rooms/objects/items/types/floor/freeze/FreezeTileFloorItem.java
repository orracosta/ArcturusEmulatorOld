package com.habboproject.server.game.rooms.objects.items.types.floor.freeze;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.executors.freeze.FreezeTileThrowBallEvent;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.freeze.FreezeGame;
import com.habboproject.server.threads.ThreadManager;

/**
 * Created by brend on 04/02/2017.
 */
public class FreezeTileFloorItem extends RoomItemFloor {
    public FreezeTileFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (entity == null || !(entity instanceof PlayerEntity) || isWiredTrigger) {
            return false;
        }

        if (this.getRoom().getGame().getInstance() == null || !(this.getRoom().getGame().getInstance() instanceof FreezeGame)) {
            return false;
        }

        if (((PlayerEntity)entity).getGameTeam() == null || ((PlayerEntity)entity).getGameTeam() == GameTeam.NONE || !((PlayerEntity)entity).getPlayer().getFreeze().canThrowBall()) {
            return false;
        }

        if (!this.getExtraData().equals("0") && !this.getExtraData().isEmpty()) {
            return false;
        }

        if (AffectedTile.tilesAdjecent(entity.getPosition().copy(), this.getPosition().copy())) {
            ThreadManager.getInstance().execute(new FreezeTileThrowBallEvent(this, (PlayerEntity)entity));
        }

        return true;
    }
}
