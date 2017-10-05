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
import com.habboproject.server.utilities.RandomInteger;

import java.util.stream.Collectors;

/**
 * Created by brend on 04/02/2017.
 */
public class FreezeBlockFloorItem extends RoomItemFloor {
    public FreezeBlockFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (entity == null || !(entity instanceof PlayerEntity) || isWiredTrigger) {
            return false;
        }

        RoomItemFloor floorTile = null;
        for (RoomItemFloor floorItem : this.getItemsOnStack().stream().filter(x -> x != null && x instanceof FreezeTileFloorItem).collect(Collectors.toList())) {
            if (floorTile != null && floorTile.getPosition().getZ() >= floorItem.getPosition().getZ())
                continue;

            floorTile = floorItem;
        }

        if (floorTile == null) {
            return false;
        }

        if (this.getRoom().getGame().getInstance() == null || !(this.getRoom().getGame().getInstance() instanceof FreezeGame)) {
            return false;
        }

        if (((PlayerEntity)entity).getGameTeam() == null || ((PlayerEntity)entity).getGameTeam() == GameTeam.NONE || !((PlayerEntity)entity).getPlayer().getFreeze().canThrowBall()) {
            return false;
        }

        if (!floorTile.getExtraData().equals("0") && !floorTile.getExtraData().isEmpty()) {
            return false;
        }

        if (AffectedTile.tilesAdjecent(entity.getPosition().copy(), floorTile.getPosition().copy())) {
            ThreadManager.getInstance().execute(new FreezeTileThrowBallEvent(floorTile, (PlayerEntity)entity));
        }

        return true;
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (this.getExtraData().isEmpty() || this.getExtraData().equals("0")) {
            return;
        }

        if (this.getRoom().getGame().getInstance() == null || !(this.getRoom().getGame().getInstance() instanceof FreezeGame)) {
            return;
        }

        if (((PlayerEntity)entity).getGameTeam() == null || ((PlayerEntity)entity).getGameTeam() == GameTeam.NONE) {
            return;
        }

        int powerUp = Integer.valueOf(this.getExtraData()) / 1000;

        if (powerUp >= 2 && powerUp <= 7) {
            this.setExtraData("" + (powerUp + 10) * 1000);
            this.sendUpdate();
            this.givePowerUp(powerUp, entity);
        }
    }

    @Override
    public void onPickup() {
        this.setExtraData("0");
    }

    @Override
    public boolean isMovementCancelled(RoomEntity entity) {
        if (entity == null)
            return true;

        if (!(entity instanceof PlayerEntity))
            return true;

        return !isBreak();
    }

    public void explode() {
        int extraData = 0;
        if (RandomInteger.nextInt(100) + 1 <= 33) {
            extraData += RandomInteger.nextInt(6) + 1;
        }

        this.setExtraData("" + (extraData + 1) * 1000);
        this.sendUpdate();
    }

    private void givePowerUp(int powerUp, RoomEntity entity) {
        switch (powerUp) {
            case 2: {
                ((PlayerEntity)entity).getPlayer().getFreeze().increaseBoost();
                break;
            }

            case 3: {
                ((PlayerEntity)entity).getPlayer().getFreeze().increaseBall();
                break;
            }

            case 4: {
                ((PlayerEntity)entity).getPlayer().getFreeze().setHorizontalExplosion(false);
                ((PlayerEntity)entity).getPlayer().getFreeze().setDiagonalExplosion(true);
                break;
            }

            case 5: {
                ((PlayerEntity)entity).getPlayer().getFreeze().setHorizontalExplosion(true);
                ((PlayerEntity)entity).getPlayer().getFreeze().setDiagonalExplosion(true);
                ((PlayerEntity)entity).getPlayer().getFreeze().setExplosionPowerUp(true);
                break;
            }

            case 6: {
                this.getRoom().getGame().increaseTeamScore(((PlayerEntity)entity).getGameTeam(), 10);
                ((PlayerEntity)entity).getPlayer().getFreeze().increaseLife();
                break;
            }

            case 7: {
                ((PlayerEntity)entity).getPlayer().getFreeze().protect();
            }
        }
    }

    public boolean isBreak() {
        return !this.getExtraData().isEmpty() && !this.getExtraData().equals("0");
    }
}
