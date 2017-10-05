package com.habboproject.server.game.rooms.objects.items.types.floor.rollable;

import com.habboproject.server.game.items.types.LowPriorityItemProcessor;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.types.ItemPathfinder;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiPuckFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballGoalFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.game.utilities.DistanceCalculator;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.habboproject.server.utilities.Direction;

import java.util.List;


public abstract class RollableFloorItem extends RoomItemFloor {
    public static final int KICK_POWER = 6;

    private PlayerEntity playerEntity;
    private boolean isRolling = false;
    private boolean skipNext = false;
    private int rollStage = -1;

    public RollableFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    public static void roll(RoomItemFloor item, Position from, Position to, Room room) {
        room.getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(from.copy(), to.copy(), item.getVirtualId(), 0, item.getVirtualId()));
    }

    public static Position calculatePosition(int x, int y, int playerRotation) {
        return Position.calculatePosition(x, y, playerRotation, false);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (this.isRolling) {
            return;
        }

        if (entity instanceof PlayerEntity && this instanceof BanzaiPuckFloorItem) {
            this.setExtraData(String.valueOf(((PlayerEntity)entity).getGameTeam().getTeamId() + 1));
            this.sendUpdate();
        }

        if (entity.getWalkingGoal().getX() == this.getPosition().getX() && entity.getWalkingGoal().getY() == this.getPosition().getY()) {
            if (this.skipNext) {
                this.onInteract(entity, 0, false);
                this.skipNext = false;
                return;
            }

            if (entity instanceof PlayerEntity) {
                this.playerEntity = (PlayerEntity)entity;
            }

            this.rollStage = 0;
            this.rollBall(entity.getPosition(), entity.getBodyRotation());
        } else {
            this.skipNext = true;
            this.onInteract(entity, 0, false);
        }
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (!this.skipNext) {
            this.rollBall(this.getPosition(), Direction.get((int)entity.getBodyRotation()).invert().num);
        }
    }

    private void rollBall(Position from, int rotation) {
        if (!DistanceCalculator.tilesTouching(this.getPosition().getX(), this.getPosition().getY(), from.getX(), from.getY())) {
            return;
        }

        this.rotation = rotation;

        this.isRolling = true;
        this.rollStage = 0;

        this.setTicks(LowPriorityItemProcessor.getProcessTime(0.075));
    }

    @Override
    public void onTickComplete() {
        if (!this.isRolling || this.rollStage == -1 || this.rollStage >= 6) {
            this.isRolling = false;
            this.rollStage = -1;
            return;
        }

        ++this.rollStage;

        Position nextPosition = this.getNextPosition(this.getPosition(), false);
        if (!this.isValidRoll(nextPosition)) {
            nextPosition = this.getNextPosition(this.getPosition(), true);
            this.setRotation(Direction.get((int)this.getRotation()).invert().num);
        }

        this.moveTo(nextPosition, this.getRotation());
        this.setTicks(LowPriorityItemProcessor.getProcessTime(this.getDelay(this.rollStage)));
    }

    private boolean isValidRoll(Position nextPosition) {
        List<Square> path = ItemPathfinder.getInstance().makePath(this, nextPosition);
        if (path == null || path.isEmpty()) {
            return false;
        }

        if (!this.getRoom().getMapping().isValidPosition(nextPosition)) {
            return false;
        }

        if (this instanceof FootballFloorItem) {
            if (this.getItemsOnStack().stream().filter(x -> x != null && x instanceof FootballGoalFloorItem).count() > 0) {
                return false;
            }
        }

        return true;
    }

    private Position getNextPosition(Position nextPosition, boolean needsReverse) {
        Position newPosition = needsReverse ? nextPosition.squareBehind(this.getRotation()) : nextPosition.squareInFront(this.getRotation());
        return newPosition;
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTriggered) {
        Position newPosition;
        if (isWiredTriggered) {
            return false;
        }

        if (this.isRolling || !entity.getPosition().touching(this.getPosition())) {
            return false;
        }

        if (entity instanceof PlayerEntity) {
            this.playerEntity = (PlayerEntity)entity;
        }

        this.isRolling = true;

        Position currentPosition = new Position(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
        if (this.isValidRoll(this.getNextPosition(currentPosition, false))) {
            newPosition = RollableFloorItem.calculatePosition(this.getPosition().getX(), this.getPosition().getY(), entity.getBodyRotation());
        } else {
            newPosition = Position.calculatePosition(this.getPosition().getX(), this.getPosition().getY(), entity.getBodyRotation(), true);
            this.setRotation(Direction.get(this.getRotation()).invert().num);
        }

        this.moveTo(newPosition, entity.getBodyRotation());

        this.isRolling = false;

        return true;
    }

    @Override
    public void onPositionChanged(Position newPosition) {
        this.isRolling = false;
        this.playerEntity = null;
        this.skipNext = false;
        this.rollStage = -1;
    }

    private void moveTo(Position position, int rotation) {
        if (!this.isValidRoll(position)) {
            return;
        }

        RoomTile newTile = this.getRoom().getMapping().getTile(position);
        if (newTile == null) {
            return;
        }

        position.setZ(newTile.getStackHeight(this));

        RollableFloorItem.roll(this, this.getPosition(), position, this.getRoom());

        RoomTile tile = this.getRoom().getMapping().getTile(this.getPosition());

        this.setRotation(rotation);
        this.getPosition().setX(position.getX());
        this.getPosition().setY(position.getY());

        if (tile != null) {
            tile.reload();
        }

        newTile.reload();

        for (RoomItemFloor floorItem : this.getRoom().getItems().getItemsOnSquare(position.getX(), position.getY())) {
            floorItem.onItemAddedToStack(this);
        }

        this.getPosition().setZ(position.getZ());

        RoomItemDao.saveItemPosition(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), this.getRotation(), this.getId());
    }

    private double getDelay(int i) {
        switch (i) {
            case 1: {
                return 0.075;
            }
            case 2: {
                return 0.2;
            }
            case 3: {
                return 0.25;
            }
            case 4: {
                return 0.3;
            }
        }
        if (i != 5) {
            return i != 6 ? 0.3 : 0.35;
        }
        return 0.35;
    }

    public PlayerEntity getPusher() {
        return this.playerEntity;
    }
}