package com.habboproject.server.game.rooms.objects.items.types.floor.rollers;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.AdvancedFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.gates.GateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.groups.GroupGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.rollable.RollableFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOnFurni;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.habboproject.server.threads.executors.roller.RollerItemExecuteEvent;
import com.habboproject.server.utilities.Direction;
import com.habboproject.server.utilities.collections.ConcurrentHashSet;

import java.util.List;
import java.util.Set;

public class RollerFloorItem extends AdvancedFloorItem<RollerItemExecuteEvent> {
    private boolean hasRollScheduled = false;
    private long lastTick = 0;

    private boolean cycleCancelled = false;

    private Set<Integer> entitiesOnRoller = new ConcurrentHashSet<>();
    private Set<Integer> movedEntities = new ConcurrentHashSet<>();

    private final RollerItemExecuteEvent event;

    public RollerFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
        this.event = new RollerItemExecuteEvent(0);
    }

    @Override
    public void onLoad() {
        event.setTotalTicks(this.getTickCount());
        this.queueEvent(event);
    }

    @Override
    public void onPlaced() {
        event.setTotalTicks(this.getTickCount());
        this.queueEvent(event);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        this.entitiesOnRoller.add(entity.getId());
        event.setTotalTicks(this.getTickCount());
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (!this.entitiesOnRoller.contains(entity.getId())) {
            return;
        }

        this.entitiesOnRoller.remove(entity.getId());
    }

    @Override
    public void onItemAddedToStack(RoomItemFloor floorItem) {
        event.setTotalTicks(this.getTickCount());
    }

    @Override
    protected void onEventComplete(RollerItemExecuteEvent event) {
        if(this.cycleCancelled) {
            this.cycleCancelled = false;
        }

        this.movedEntities.clear();

        this.handleEntities();

        if(!cycleCancelled) {
            this.handleItems();
        }

        event.setTotalTicks(this.getTickCount());

        this.queueEvent(event);
    }

    private void handleEntities() {
        Position sqInfront = this.getPosition().squareInFront(this.getRotation());

        if (!this.getRoom().getMapping().isValidPosition(sqInfront)) {
            return;
        }

        boolean retry = false;

        final Direction direction = Direction.get(this.getRotation());

        List<RoomEntity> entities = this.getRoom().getEntities().getEntitiesAt(this.getPosition());

        for (RoomEntity entity : entities) {
            if (entity.getPosition().getX() != this.getPosition().getX() && entity.getPosition().getY() != this.getPosition().getY()) {
                continue;
            }

            if (entity.getPositionToSet() != null) {
                continue;
            }

            if (!this.getRoom().getMapping().isValidStep(entity, entity.getPosition(), sqInfront, true, false, false, true, false)) {
                retry = true;
                break;
            }

            if (this.getRoom().getEntities().positionHasEntity(sqInfront)) {
                retry = true;
                break;
            }

            if (entity.isWalking()) {
                continue;
            }

            if (sqInfront.getX() == this.getRoom().getModel().getDoorX() && sqInfront.getY() == this.getRoom().getModel().getDoorY()) {
                entity.leaveRoom(false, false, true);
                continue;
            }

            WiredTriggerWalksOffFurni.executeTriggers(entity, this);

            boolean hasRoller = false;
            boolean rollerIsFacing = false;
            boolean stopRoller = false;

            int itemsAtTile = 0;

            for (RoomItemFloor nextItem : this.getRoom().getItems().getItemsOnSquare(sqInfront.getX(), sqInfront.getY())) {
                if (nextItem instanceof GroupGateFloorItem) break;

                if (nextItem instanceof GateFloorItem) {
                    if (nextItem.getExtraData().equals("0")) {
                        stopRoller = true;
                        break;
                    }
                }

                itemsAtTile++;

                if (nextItem instanceof RollerFloorItem) {
                    hasRoller = true;

                    final Direction rollerDirection = Direction.get(nextItem.getRotation());
                    final Direction rollerInverted = rollerDirection.invert();

                    if (rollerInverted == direction) {
                        rollerIsFacing = true;
                    }
                }

                WiredTriggerWalksOnFurni.executeTriggers(entity, nextItem);

                nextItem.onEntityStepOn(entity);
            }

            if (stopRoller) break;

            if (hasRoller && rollerIsFacing) {
                if (itemsAtTile > 1) {
                    retry = true;
                    break;
                }
            }

            final double toHeight = this.getRoom().getMapping().getTile(sqInfront.getX(), sqInfront.getY()).getWalkHeight();

            final RoomTile oldTile = this.getRoom().getMapping().getTile(entity.getPosition().getX(), entity.getPosition().getY());
            final RoomTile newTile = this.getRoom().getMapping().getTile(sqInfront.getX(), sqInfront.getY());

            if (oldTile != null) {
                oldTile.getEntities().remove(entity);
            }

            if (newTile != null) {
                newTile.getEntities().add(entity);
            }

            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(entity.getPosition(), new Position(sqInfront.getX(), sqInfront.getY(), toHeight), this.getVirtualId(), entity.getId(), 0));
            entity.setPosition(new Position(sqInfront.getX(), sqInfront.getY(), toHeight));

            this.onEntityStepOff(entity);

            movedEntities.add(entity.getId());
        }

        if (retry) {
            this.cycleCancelled = true;
        }
    }

    private void handleItems() {
        List<RoomItemFloor> floorItems = this.getRoom().getItems().getItemsOnSquare(this.getPosition().getX(), this.getPosition().getY());

        if (floorItems.size() < 2) {
            return;
        }

        // quick check illegal use of rollers
        int rollerCount = 0;
        for (RoomItemFloor f : floorItems) {
            if (f instanceof RollerFloorItem) {
                rollerCount++;
            }
        }

        if (rollerCount > 1) {
            return;
        }

        final Position sqInfront = this.getPosition().squareInFront(this.getRotation());
        List<RoomItemFloor> itemsSq = this.getRoom().getItems().getItemsOnSquare(sqInfront.getX(), sqInfront.getY());

        boolean noItemsOnNext = false;

        for (RoomItemFloor floor : floorItems) {
            if (floor.getPosition().getX() != this.getPosition().getX() && floor.getPosition().getY() != this.getPosition().getY()) {
                continue;
            }

            if (floor instanceof RollerFloorItem || floor.getPosition().getZ() <= this.getPosition().getZ()) {
                continue;
            }

            if (!floor.getDefinition().canStack() && !(floor instanceof RollableFloorItem)) {
                if (floor.getTile().getTopItem() != floor.getId())
                    continue;
            }

            double height = floor.getPosition().getZ();

            boolean hasRoller = false;

            for (RoomItemFloor iq : itemsSq) {
                if (iq instanceof RollerFloorItem) {
                    hasRoller = true;

                    if (iq.getPosition().getZ() != this.getPosition().getZ()) {
                        height -= this.getPosition().getZ();
                        height += iq.getPosition().getZ();
                    }
                }
            }

            if (!hasRoller || noItemsOnNext) {
                height -= 0.5;
                noItemsOnNext = true;
            }

            if (hasRoller) {// && rollerIsFacing) {
                if (itemsSq.size() > 1) {
                    return;
                }
            }

            if (!this.getRoom().getMapping().isValidStep(null, new Position(floor.getPosition().getX(), floor.getPosition().getY(), floor.getPosition().getZ()), sqInfront, true, false, false, true, true)) {
                return;
            }

            if (this.getRoom().getEntities().positionHasEntity(sqInfront, this.movedEntities)) {
                return;
            }

            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(new Position(floor.getPosition().getX(), floor.getPosition().getY(), floor.getPosition().getZ()), new Position(sqInfront.getX(), sqInfront.getY(), height), this.getVirtualId(), 0, floor.getVirtualId()));

            floor.getPosition().setX(sqInfront.getX());
            floor.getPosition().setY(sqInfront.getY());
            floor.getPosition().setZ(height);

            RoomItemDao.saveItemPosition(floor.getPosition().getX(), floor.getPosition().getY(), floor.getPosition().getZ(), floor.getRotation(), floor.getId());
        }

        this.getRoom().getMapping().updateTile(this.getPosition().getX(), this.getPosition().getY());
        this.getRoom().getMapping().updateTile(sqInfront.getX(), sqInfront.getY());

        for (RoomItemFloor nextItem : this.getRoom().getItems().getItemsOnSquare(sqInfront.getX(), sqInfront.getY())) {
            for (RoomItemFloor floor : floorItems) {
                nextItem.onItemAddedToStack(floor);
            }
        }
    }

    private int getTickCount() {
        return RoomItemFactory.getProcessTime((this.getRoom().hasAttribute("customRollerSpeed") ? (int) this.getRoom().getAttribute("customRollerSpeed") : 4) / 2);
    }
}
