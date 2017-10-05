package com.habboproject.server.game.rooms.objects.items.types.floor.others;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.utilities.RandomInteger;


public class VendingMachineFloorItem extends RoomItemFloor {
    private RoomEntity vendingEntity;
    private int state = -1;

    public VendingMachineFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (isWiredTrigger || entity == null) return false;

        Position posInFront = this.getPosition().squareInFront(this.getRotation());

        if (!posInFront.equals(entity.getPosition())) {
            entity.moveTo(posInFront.getX(), posInFront.getY());

            try {
                this.getRoom().getMapping().getTile(posInFront.getX(), posInFront.getY()).scheduleEvent(entity.getId(), (e) -> onInteract(e, requestData, false));
            } catch(Exception e) {
                // this isn't important, if we can't find the tile then we might as well just end it here.
            }
            return false;
        }

        int rotation = Position.calculateRotation(entity.getPosition().getX(), entity.getPosition().getY(), this.getPosition().getX(), this.getPosition().getY(), false);

        if (!entity.hasStatus(RoomEntityStatus.SIT) && !entity.hasStatus(RoomEntityStatus.LAY)) {
            entity.setBodyRotation(rotation);
            entity.setHeadRotation(rotation);

            entity.markNeedsUpdate();
        }

        this.vendingEntity = entity;

        this.state = 0;
        this.setTicks(RoomItemFactory.getProcessTime(1));
        return true;
    }

    @Override
    public void onTickComplete() {
        switch (this.state) {
            case 0: {
                this.setExtraData("1");
                this.sendUpdate();

                this.state = 1;
                this.setTicks(RoomItemFactory.getProcessTime(0.5));
                break;
            }

            case 1: {
                if (this.getDefinition().getVendingIds().length != 0) {
                    int vendingId = Integer.parseInt(this.getDefinition().getVendingIds()[RandomInteger.getRandom(0, this.getDefinition().getVendingIds().length - 1)].trim());
                    vendingEntity.carryItem(vendingId);
                }

                this.state = 2;
                this.setTicks(RoomItemFactory.getProcessTime(0.5));
                break;
            }

            case 2: {
                this.setExtraData("0");
                this.sendUpdate();

                this.state = 0;
                this.vendingEntity = null;
                break;
            }
        }
    }

    @Override
    public void onPlaced() {
        this.setExtraData("0");
    }
}
