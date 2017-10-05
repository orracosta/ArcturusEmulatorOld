package com.habboproject.server.game.rooms.objects.items.types.floor.others;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;

import java.util.Random;


public class DiceFloorItem extends RoomItemFloor {
    private boolean isInUse = false;
    private int rigNumber = -1;

    public DiceFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!this.getPosition().touching(entity)) {
                entity.moveTo(this.getPosition().squareInFront(this.rotation).getX(), this.getPosition().squareBehind(this.rotation).getY());
                return false;
            }
        }

        if (this.isInUse) {
            return false;
        }

        if (requestData >= 0) {
            if (!"-1".equals(this.getExtraData())) {
                this.setExtraData("-1");
                this.sendUpdate();

                this.isInUse = true;

                if (entity != null) {
                    if (entity.hasAttribute("diceRoll")) {
                        this.rigNumber = (int) entity.getAttribute("diceRoll");
                        entity.removeAttribute("diceRoll");
                    }
                }

                this.setTicks(RoomItemFactory.getProcessTime(2.5));
            }
        } else {
            this.setExtraData("0");
            this.sendUpdate();

            this.saveData();
        }

        return true;
    }

    @Override
    public void onPlaced() {
        if (!"0".equals(this.getExtraData())) {
            this.setExtraData("0");
        }
    }

    @Override
    public void onPickup() {
        this.cancelTicks();
    }

    @Override
    public void onTickComplete() {
        int num = new Random().nextInt(6) + 1;

        this.setExtraData(Integer.toString(this.rigNumber == -1 ? num : this.rigNumber));
        this.sendUpdate();

        this.saveData();

        if (this.rigNumber != -1) this.rigNumber = -1;

        this.isInUse = false;
    }
}
