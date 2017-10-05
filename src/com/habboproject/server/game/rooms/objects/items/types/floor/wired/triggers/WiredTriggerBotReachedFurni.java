package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 04/02/2017.
 */
public class WiredTriggerBotReachedFurni extends WiredTriggerItem {
    public WiredTriggerBotReachedFurni(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean suppliesPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 13;
    }

    public static boolean executeTriggers(BotEntity botEntity, RoomItemFloor floorItem) {
        boolean wasExecuted = false;
        for (RoomItemFloor wiredItem : botEntity.getRoom().getItems().getByClass(WiredTriggerBotReachedFurni.class)) {
            WiredTriggerBotReachedFurni trigger = (WiredTriggerBotReachedFurni)wiredItem;

            if (trigger.getWiredData().getText().isEmpty())
                continue;

            String botName = trigger.getWiredData().getText();
            if (botEntity == null || !botEntity.getUsername().equals(botName) || floorItem == null)
                continue;

            wasExecuted = trigger.evaluate(botEntity, floorItem);
        }

        return wasExecuted;
    }
}
