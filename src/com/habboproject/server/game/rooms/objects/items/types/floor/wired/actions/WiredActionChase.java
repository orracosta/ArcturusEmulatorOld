package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.google.common.collect.Lists;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.Pathfinder;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.types.ItemPathfinder;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerCollision;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;
import com.habboproject.server.utilities.RandomInteger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WiredActionChase extends WiredActionItem {
    private int targetId = -1;

    public WiredActionChase(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 8;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (this.getWiredData().getSelectedIds().size() == 0) return;

        List<Long> toRemove = Lists.newArrayList();
        for (Iterator localIterator = getWiredData().getSelectedIds().iterator(); localIterator.hasNext();) {
            long itemId = (Long) localIterator.next();
            RoomItemFloor floorItem = getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                toRemove.add(itemId);
            } else {
                boolean hasEntityOnTile = false;
                if ((hasEntityOnTile = floorItem.getEntitiesOnItem().size() > 0)) {
                    for (Iterator iterator = floorItem.getEntitiesOnItem().iterator(); iterator.hasNext();) {
                        RoomEntity onTile = (RoomEntity) iterator.next();
                        if (onTile instanceof PlayerEntity) {
                            PlayerEntity playerEntity = (PlayerEntity) onTile;
                            if (floorItem.getCollision() != playerEntity) {
                                floorItem.setCollision(playerEntity);
                                WiredTriggerCollision.executeTriggers(playerEntity);
                            }
                        }
                    }
                }

                if (hasEntityOnTile) {
                    floorItem.nullifyCollision();
                    continue;
                }

                PlayerEntity nearestEntity = floorItem.nearestPlayerEntity();
                Position positionFrom = floorItem.getPosition().copy();

                if (nearestEntity != null) {
                    if (this.isCollided(nearestEntity, floorItem)) {
                        if (floorItem.getCollision() != nearestEntity) {
                            floorItem.setCollision(nearestEntity);

                            WiredTriggerCollision.executeTriggers(nearestEntity);
                        }

                        continue;
                    }

                    this.targetId = nearestEntity.getId();

                    List<Square> tilesToEntity = ItemPathfinder.getInstance().makePath(floorItem, nearestEntity.getPosition(), Pathfinder.DISABLE_DIAGONAL, false);

                    if (tilesToEntity != null && tilesToEntity.size() != 0) {
                        Position positionTo = new Position(tilesToEntity.get(0).x, tilesToEntity.get(0).y);

                        this.moveToTile(floorItem, positionFrom, positionTo);
                        tilesToEntity.clear();
                    } else {
                        this.moveToTile(floorItem, positionFrom, null);
                    }
                } else {
                    this.moveToTile(floorItem, positionFrom, null);
                }
            }
        }

        if (toRemove.size() > 0) {
            for (long itemId : toRemove) {
                if (getWiredData().getSelectedIds().contains(itemId))
                    getWiredData().getSelectedIds().remove(itemId);
            }
        }
    }

    public boolean isCollided(PlayerEntity entity, RoomItemFloor floorItem) {
        return (AffectedTile.tilesAdjecent(entity.getPosition(), floorItem.getPosition()) && (entity.getPosition().getX() == floorItem.getPosition().getX() || entity.getPosition().getY() == floorItem.getPosition().getY())) || entity.getPosition().getX() == floorItem.getPosition().getX() && entity.getPosition().getY() == floorItem.getPosition().getY();
    }

    private void moveToTile(RoomItemFloor floorItem, Position from, Position to) {
        if (to == null) {
            for (int i = 0; i < 16; i++) {
                if (to != null) break;

                to = this.random(floorItem, from);
            }

            if (to == null) return;
        }

        if (this.getRoom().getItems().moveFloorItem(floorItem.getId(), to, floorItem.getRotation(), true)) {
            to.setZ(floorItem.getPosition().getZ());

            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(from, to, 0, 0, floorItem.getVirtualId()));
        }

        floorItem.nullifyCollision();
    }

    private Position random(RoomItemFloor floorItem, Position from) {
        int randomDirection = RandomInteger.getRandom(0, 3) * 2;
        Position newPosition = from.squareInFront(randomDirection);
        RoomTile tile = floorItem.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());

        if (tile != null && tile.isReachable(floorItem)) {
            return newPosition;
        }

        return null;
    }

    public int getTargetId() {
        return this.targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
}
