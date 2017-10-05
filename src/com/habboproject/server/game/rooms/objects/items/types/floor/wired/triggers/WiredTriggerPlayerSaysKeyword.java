package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import org.apache.commons.lang3.StringUtils;


public class WiredTriggerPlayerSaysKeyword extends WiredTriggerItem {
    public static final int PARAM_OWNERONLY = 0;

    public WiredTriggerPlayerSaysKeyword(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 0;
    }

    public static boolean executeTriggers(PlayerEntity playerEntity, String message) {
        boolean wasExecuted = false;
        for (RoomItemFloor floorItem : playerEntity.getRoom().getItems().getByClass(WiredTriggerPlayerSaysKeyword.class)) {
            WiredTriggerPlayerSaysKeyword trigger = (WiredTriggerPlayerSaysKeyword)floorItem;

            boolean ownerOnly = trigger.getWiredData().getParams().containsKey(0) && trigger.getWiredData().getParams().get(0) != 0;
            boolean isOwner = playerEntity.getPlayerId() == trigger.getRoom().getData().getOwnerId();

            if (ownerOnly && !isOwner || trigger.getWiredData().getText().isEmpty() || !StringUtils.containsIgnoreCase(message, trigger.getWiredData().getText()))
                continue;

            wasExecuted = trigger.evaluate(playerEntity, message);
        }

        if (wasExecuted) {
            playerEntity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), message));
        }

        return wasExecuted;
    }
}
