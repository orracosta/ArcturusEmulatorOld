package com.habboproject.server.game.rooms.objects.items.types.floor.wired.addons;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.utilities.RandomInteger;


public class WiredAddonColourWheel extends RoomItemFloor {
    private static final int TIMEOUT = 4;

    public WiredAddonColourWheel(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, "0");
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger && entity != null) {
            if (!this.getPosition().touching(entity)) {
                entity.moveTo(this.getPosition().squareBehind(this.getRotation()).getX(), this.getPosition().squareBehind(this.rotation).getY());
                return true;
            }
        }

        this.setExtraData("9");
        this.sendUpdate();

        this.setTicks(RoomItemFactory.getProcessTime(TIMEOUT / 2));
        return true;
    }

    @Override
    public void onTickComplete() {
        final int randomInteger = RandomInteger.getRandom(1, 8);

        this.setExtraData(randomInteger + "");
        this.sendUpdate();
    }
}
