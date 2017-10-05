package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;
import org.apache.commons.lang3.StringUtils;

public class WiredTriggerEnterRoom extends WiredTriggerItem {
    public WiredTriggerEnterRoom(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    public static void executeTriggers(PlayerEntity playerEntity) {
        if(playerEntity == null || playerEntity.getRoom() == null || playerEntity.getRoom().getItems() == null) {
            return;
        }

        for (RoomItemFloor floorItem : playerEntity.getRoom().getItems().getByClass(WiredTriggerEnterRoom.class)) {
            if (!(floorItem instanceof WiredTriggerEnterRoom)) continue;

            WiredTriggerEnterRoom trigger = ((WiredTriggerEnterRoom) floorItem);

            if (trigger.getWiredData().getText().isEmpty() || trigger.getWiredData().getText().equals(playerEntity.getUsername())) {
                trigger.evaluate(playerEntity, null);
            }
        }
    }
}
