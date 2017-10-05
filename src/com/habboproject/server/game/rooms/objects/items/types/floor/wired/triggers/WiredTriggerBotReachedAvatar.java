package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 04/02/2017.
 */
public class WiredTriggerBotReachedAvatar extends WiredTriggerItem {
    public WiredTriggerBotReachedAvatar(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 14;
    }

    public static boolean executeTriggers(BotEntity botEntity, PlayerEntity reachedPlayer) {
        boolean wasExecuted = false;
        for (RoomItemFloor floorItem : botEntity.getRoom().getItems().getByClass(WiredTriggerBotReachedAvatar.class)) {
            WiredTriggerBotReachedAvatar trigger = (WiredTriggerBotReachedAvatar)floorItem;

            if (trigger.getWiredData().getText().isEmpty())
                continue;

            String botName = trigger.getWiredData().getText();
            if (botEntity == null || !botEntity.getUsername().equals(botName) || reachedPlayer == null)
                continue;

            wasExecuted = trigger.evaluate(reachedPlayer, null);
        }

        return wasExecuted;
    }
}
