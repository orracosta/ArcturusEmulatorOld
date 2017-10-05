package com.habboproject.server.threads.executors.freeze;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.FreezeBlockFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.FreezeTileFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.freeze.FreezeGame;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/02/2017.
 */
public class FreezeTileBallExplosionEvent implements CometThread {
    private final RoomItemFloor floorItem;
    private final PlayerEntity entity;
    private final int radius;

    public FreezeTileBallExplosionEvent(RoomItemFloor floorItem, PlayerEntity entity, int radius) {
        this.floorItem = floorItem;
        this.entity = entity;
        this.radius = radius;
    }

    @Override
    public void run() {
        FreezeGame freeze = (FreezeGame)this.floorItem.getRoom().getGame().getInstance();

        ArrayList<AffectedTile> affectedTiles = Lists.newArrayList();
        ArrayList<RoomItemFloor> affectedItems = Lists.newArrayList();

        try {
            if (freeze == null) {
                return;
            }

            if (this.entity.getPlayer().getFreeze().horizontalExplosion()) {
                affectedTiles.addAll(AffectedTile.getAffectedTilesByExplosion(this.radius, this.floorItem, false));
            }

            if (this.entity.getPlayer().getFreeze().diagonalExplosion()) {
                this.entity.getPlayer().getFreeze().setHorizontalExplosion(true);
                this.entity.getPlayer().getFreeze().setDiagonalExplosion(false);

                affectedTiles.addAll(AffectedTile.getAffectedTilesByExplosion(this.radius, this.floorItem, true));
            }

            for (AffectedTile tile : affectedTiles) {
                for (RoomItemFloor item : this.floorItem.getRoom().getItems().getItemsOnSquare(tile.x, tile.y)) {
                    if (item == null || !(item instanceof FreezeTileFloorItem) && !(item instanceof FreezeBlockFloorItem)) continue;
                    if (item instanceof FreezeTileFloorItem) {
                        item.setExtraData("11000");
                        item.sendUpdate();

                        affectedItems.add(item);

                        List<RoomEntity> affectedEntities = this.floorItem.getRoom().getEntities().getEntitiesAt(new Position(tile.x, tile.y));
                        for (RoomEntity affectedEntity : affectedEntities) {
                            if (affectedEntity == null || !(affectedEntity instanceof PlayerEntity) || ((PlayerEntity)affectedEntity).getGameTeam() == null || ((PlayerEntity)affectedEntity).getGameTeam() == GameTeam.NONE || !((PlayerEntity)affectedEntity).getPlayer().getFreeze().canFreeze())
                                continue;

                            ((PlayerEntity)affectedEntity).getPlayer().getFreeze().freeze();
                        }
                    }

                    if (!item.getExtraData().isEmpty() && !item.getExtraData().equals("0"))
                        continue;

                    ((FreezeBlockFloorItem)item).explode();
                }
            }

            this.entity.getPlayer().getFreeze().increaseBall();

            ThreadManager.getInstance().executeSchedule(new FreezeResetTileEvent(affectedItems), 1000, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {

        }
    }
}
