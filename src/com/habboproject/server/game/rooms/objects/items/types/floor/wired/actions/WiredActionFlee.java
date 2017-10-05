package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
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
import java.util.Map;

public class WiredActionFlee extends WiredActionItem {
    public WiredActionFlee(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 12;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (getWiredData().getSelectedIds().size() == 0) {
            return;
        }

        List<Long> toRemove = Lists.newArrayList();
        for (Iterator localIterator = getWiredData().getSelectedIds().iterator(); localIterator.hasNext();) {
            long itemId = (Long) localIterator.next();

            RoomItemFloor floorItem = getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                toRemove.add(itemId);
            } else {
                PlayerEntity nearestEntity = floorItem.nearestPlayerEntity();
                Position positionFrom = floorItem.getPosition().copy();

                if (nearestEntity != null && nearestEntity.getPosition() != null) {
                    Position newCoordinate = floorItem.getPosition().squareBehind(Position.calculateRotation(floorItem.getPosition().getX(), floorItem.getPosition().getY(), nearestEntity.getPosition().getX(), nearestEntity.getPosition().getY(), false));

                    List<Square> tilesToEntity = ItemPathfinder.getInstance().makePath(floorItem, newCoordinate, (byte) 0, false);
                    if ((tilesToEntity != null) && (tilesToEntity.size() != 0)) {
                        Position positionTo = new Position(tilesToEntity.get(0).x, tilesToEntity.get(0).y);
                        moveToTile(floorItem, positionFrom, positionTo);
                        tilesToEntity.clear();
                    } else {
                        moveToTile(floorItem, positionFrom, null);
                    }
                } else {
                    moveToTile(floorItem, positionFrom, null);
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
        return AffectedTile.tilesAdjecent(entity.getPosition(), floorItem.getPosition()) && (entity.getPosition().getX() == floorItem.getPosition().getX() ||
                entity.getPosition().getY() == floorItem.getPosition().getY()) || entity.getPosition().getX() == floorItem.getPosition().getX() && entity.getPosition().getY() == floorItem.getPosition().getY();
    }

    private void moveToTile(RoomItemFloor floorItem, Position from, Position to) {
        if (from == null) {
            return;
        }

        if (to == null) {
            for (int i = 0; i < 16; i++) {
                if (to != null)
                    break;
                to = random(floorItem, from);
            }

            if (to == null) {
                return;
            }
        }

        if (getRoom().getItems().moveFloorItem(floorItem.getId(), to, floorItem.getRotation(), true)) {
            final Map<Integer, Double> items = Maps.newHashMap();

            items.put(floorItem.getVirtualId(), floorItem.getPosition().getZ());

            getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(from, to, 0, 0, items));
        }

        PlayerEntity nearestEntity = floorItem.nearestPlayerEntity();
        if ((nearestEntity != null) && (isCollided(nearestEntity, floorItem))) {
            floorItem.setCollision(nearestEntity);
            WiredTriggerCollision.executeTriggers(nearestEntity);
        }

        floorItem.nullifyCollision();
    }

    private Position random(RoomItemFloor floorItem, Position from) {
        int randomDirection = RandomInteger.getRandom(0, 3) * 2;
        Position newPosition = from.squareBehind(randomDirection);
        RoomTile tile = floorItem.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());

        if ((tile != null) && (tile.isReachable(floorItem))) {
            return newPosition;
        }

        return null;
    }
}
