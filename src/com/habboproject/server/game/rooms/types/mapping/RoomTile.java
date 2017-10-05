package com.habboproject.server.game.rooms.types.mapping;

import com.habboproject.server.game.rooms.objects.RoomObject;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.types.ItemPathfinder;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.AdjustableHeightFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.MagicStackFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballGoalFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.FreezeBlockFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.gates.GateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.groups.GroupGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.BedFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.SeatFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.pet.PetBreedingBoxFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.snowboarding.SnowboardJumpFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.tiles.RoomTileState;
import com.habboproject.server.utilities.collections.ConcurrentHashSet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RoomTile {
    private RoomMapping mappingInstance;
    private Position position;

    private RoomEntityMovementNode movementNode;
    private RoomTileStatusType status;
    private RoomTileState state;

    private boolean canStack;

    private long topItem = 0;
    private double stackHeight = 0d;

    private long originalTopItem = 0;
    private double originalHeight = 0d;

    private Position redirect = null;

    private boolean canPlaceItemHere = false;
    private boolean hasItems = false;
    private boolean hasMagicTile = false;

    private boolean hasAdjustableHeight = false;

    private List<RoomItemFloor> items;
    public Set<RoomEntity> entities;

    private Map<Integer, Consumer<RoomEntity>> pendingEvents = new ConcurrentHashMap<>();

    public RoomTile(RoomMapping mappingInstance, Position position) {
        this.mappingInstance = mappingInstance;
        this.position = position;
        this.entities = new ConcurrentHashSet<>();
        this.items = new ArrayList<>(); // maybe change this in the future..

        this.reload();
    }

    public void reload() {
        // reset the tile data
        this.hasItems = false;
        this.redirect = null;
        this.movementNode = RoomEntityMovementNode.OPEN;
        this.status = RoomTileStatusType.NONE;
        this.canStack = true;
        this.hasMagicTile = false;
        this.topItem = 0;
        this.originalHeight = 0d;
        this.originalTopItem = 0;
        this.stackHeight = 0d;
        this.hasAdjustableHeight = false;

        if (this.mappingInstance.getModel().getSquareState()[this.getPosition().getX()][this.getPosition().getY()] == null) {
            this.canPlaceItemHere = false;
            this.state = RoomTileState.INVALID;
        } else {
            this.canPlaceItemHere = this.mappingInstance.getModel().getSquareState()[this.getPosition().getX()][this.getPosition().getY()].equals(RoomTileState.VALID);
            this.state = this.mappingInstance.getModel().getSquareState()[this.getPosition().getX()][this.getPosition().getY()];
        }

        // component item is an item that can be used along with an item that overrides the height.
        boolean hasComponentItem = false;

        double highestHeight = 0d;
        long highestItem = 0;

        Double staticOverrideHeight = null;
        Double overrideHeight = null;

        this.items.clear();

        for (Map.Entry<Long, RoomItemFloor> itemEntry : mappingInstance.getRoom().getItems().getFloorItems().entrySet()) {
            final RoomItemFloor item = itemEntry.getValue();

            if (item == null || item.getDefinition() == null) continue; // it's null!

            if (item.getPosition().getX() == this.position.getX() && item.getPosition().getY() == this.position.getY()) {
                items.add(item);
            } else {
                List<AffectedTile> affectedTiles = AffectedTile.getAffectedTilesAt(
                        item.getDefinition().getLength(), item.getDefinition().getWidth(), item.getPosition().getX(), item.getPosition().getY(), item.getRotation());

                for (AffectedTile tile : affectedTiles) {
                    if (this.position.getX() == tile.x && this.position.getY() == tile.y) {
                        if (!items.contains(item)) {
                            items.add(item);
                        }
                    }
                }
            }
        }

        for (Iterator localIterator = this.items.iterator(); localIterator.hasNext();) {
            RoomItemFloor item = (RoomItemFloor) localIterator.next();

            if (item == null || item.getDefinition() == null)
                continue;

            this.hasItems = true;

            double totalHeight = item.getTotalHeight();

            if (totalHeight > highestHeight) {
                highestHeight = totalHeight;
                highestItem = item.getId();
            }

            boolean isGate = item instanceof GateFloorItem;

            if (item instanceof MagicStackFloorItem) {
                this.hasMagicTile = true;
            }

            if (!item.getDefinition().canWalk() && !isGate) {
                if (highestItem == item.getId())
                    movementNode = RoomEntityMovementNode.CLOSED;
            }

            switch (item.getDefinition().getInteraction().toLowerCase()) {
                case "bed":
                    status = RoomTileStatusType.LAY;
                    movementNode = RoomEntityMovementNode.END_OF_ROUTE;

                    if (item.getRotation() == 2 || item.getRotation() == 6) {
                        this.redirect = new Position(item.getPosition().getX(), this.getPosition().getY());
                    } else if (item.getRotation() == 0 || item.getRotation() == 4) {
                        this.redirect = new Position(this.getPosition().getX(), item.getPosition().getY());
                    }

                    break;

                case "gate":
                    movementNode = ((GateFloorItem) item).getExtraData().equals("1") ? RoomEntityMovementNode.OPEN : RoomEntityMovementNode.CLOSED;
                    break;

                case "group_gate":
                    movementNode = ((GroupGateFloorItem) item).isOpen() ? RoomEntityMovementNode.OPEN : RoomEntityMovementNode.CLOSED;
                    break;

                case "onewaygate":
                    movementNode = RoomEntityMovementNode.CLOSED;
                    break;

                case "wf_pyramid":
                    movementNode = item.getExtraData().equals("1") ? RoomEntityMovementNode.OPEN : RoomEntityMovementNode.CLOSED;
                    break;

                case "freeze_block":
                    movementNode = ((FreezeBlockFloorItem) item).isBreak() ? RoomEntityMovementNode.OPEN : RoomEntityMovementNode.CLOSED;
                    break;
            }

            if (item instanceof SnowboardJumpFloorItem) {
                hasComponentItem = true;
            }

            if (item.getDefinition().canSit()) {
                status = RoomTileStatusType.SIT;
                movementNode = RoomEntityMovementNode.END_OF_ROUTE;
            }

            if (item.getDefinition().getInteraction().equals("bed")) {
                status = RoomTileStatusType.LAY;
                movementNode = RoomEntityMovementNode.END_OF_ROUTE;
            }

            if (!item.getDefinition().canStack()) {
                this.canStack = false;
            }
        }

        this.stackHeight = highestHeight;
        this.topItem = highestItem;

        if (this.stackHeight == 0d) {
            this.stackHeight = this.mappingInstance.getModel().getSquareHeight()[this.position.getX()][this.position.getY()];
        }

        if (this.originalHeight == 0)
            this.originalHeight = this.stackHeight;
    }

    public void dispose() {
        this.pendingEvents.clear();
        this.items.clear();
        this.entities.clear();
    }

    public void onEntityEntersTile(RoomEntity entity) {
        if (this.pendingEvents.containsKey(entity.getId())) {
            this.pendingEvents.get(entity.getId()).accept(entity);
            this.pendingEvents.remove(entity.getId());
        }
    }

    public void scheduleEvent(int entityId, Consumer<RoomEntity> event) {
        this.pendingEvents.put(entityId, event);
    }

    public RoomEntityMovementNode getMovementNode() {
        return this.movementNode;
    }

    public double getStackHeight(RoomItemFloor newItem) {
        double stackHeight = this.mappingInstance.getModel().getSquareHeight()[this.position.getX()][this.position.getY()];

        for (RoomItemFloor floorItem : this.mappingInstance.getRoom().getItems().getItemsOnSquare(this.position.getX(), this.position.getY())) {
            if (floorItem == null)
                continue;

            if (newItem != null && newItem.getId() == floorItem.getId())
                continue;

            if (floorItem instanceof MagicStackFloorItem) {
                stackHeight = floorItem.getTotalHeight();
                break;
            }

            if (stackHeight >= floorItem.getTotalHeight())
                continue;

            stackHeight = floorItem.getTotalHeight();
        }

        return stackHeight;
    }

    public double getWalkHeight() {
        double height = this.getStackHeight(null);

        RoomItemFloor roomItemFloor = this.mappingInstance.getRoom().getItems().getFloorItem(this.topItem);
        if (roomItemFloor != null && (roomItemFloor.getDefinition().canSit() || roomItemFloor instanceof BedFloorItem || roomItemFloor instanceof SnowboardJumpFloorItem)) {
            height = roomItemFloor instanceof SnowboardJumpFloorItem ? (height += 1.0) : (height -= roomItemFloor.getDefinition().getHeight());
        }

        return height;
    }

    public boolean isReachable(RoomObject object) {
        List<Square> path = ItemPathfinder.getInstance().makePath(object, this.position);
        return path != null && path.size() > 0;
    }

    public RoomEntity getEntity() {
        for (RoomEntity entity : this.getEntities()) {
            return entity;
        }

        return null;
    }

    public Set<RoomEntity> getEntities() {
        return this.entities;
    }

    public RoomTileStatusType getStatus() {
        return this.status;
    }

    public Position getPosition() {
        return this.position;
    }

    public boolean canStack() {
        return this.canStack;
    }

    public long getTopItem() {
        return this.topItem;
    }

    public RoomItemFloor getTopItemInstance() {
        return this.mappingInstance.getRoom().getItems().getFloorItem(this.getTopItem());
    }

    public Position getRedirect() {
        return redirect;
    }

    public double getOriginalHeight() {
        return originalHeight;
    }

    public boolean canPlaceItemHere() {
        return canPlaceItemHere;
    }

    public boolean hasItems() {
        return hasItems;
    }

    public double getTileHeight() {
        return this.mappingInstance.getModel().getSquareHeight()[this.position.getX()][this.position.getY()];
    }

    public List<RoomItemFloor> getItems() {
        return items;
    }

    public RoomTileState getState() {
        return state;
    }

    public boolean isBlocked(RoomEntity entity) {
        boolean isBlocked = this.getMovementNode() == RoomEntityMovementNode.CLOSED;

        if (entity == null || !(entity instanceof PlayerEntity) && !(entity instanceof PetEntity))
            return isBlocked;

        for (Iterator iterator = this.getItems().stream().filter(item -> this.isPossibleOverride(item)).iterator(); iterator.hasNext();) {
            RoomItemFloor floorItem = (RoomItemFloor) iterator.next();
            if (floorItem != null) {
                if (!floorItem.isMovementCancelled(entity)) {
                    isBlocked = false;
                    break;
                }
            }
        }

        return isBlocked;
    }

    private boolean isPossibleOverride(RoomItemFloor item) {
        return (item instanceof GroupGateFloorItem || item instanceof GateFloorItem || item instanceof FreezeBlockFloorItem || item instanceof FootballGoalFloorItem || item instanceof PetBreedingBoxFloorItem);
    }
}
