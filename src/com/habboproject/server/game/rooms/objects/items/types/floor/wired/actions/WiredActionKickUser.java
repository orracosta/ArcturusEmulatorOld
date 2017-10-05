package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;


public class WiredActionKickUser extends WiredActionShowMessage {
    public WiredActionKickUser(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        this.isWhisperBubble = true;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (event.entity == null || !(event.entity instanceof PlayerEntity)) {
            return;
        }

        if(event.type == 1) {
            event.entity.leaveRoom(false, true, true);
            return;
        }

        PlayerEntity playerEntity = (PlayerEntity) event.entity;

        String kickException = "";

        if (this.getRoom().getData().getOwnerId() == playerEntity.getPlayerId()) {
            kickException = "Room owner";
        }

        if (kickException.isEmpty()) {
            super.onEventComplete(event);

            event.entity.applyEffect(new PlayerEffect(4, 5));
            event.type = 1;

            event.setTotalTicks(RoomItemFactory.getProcessTime(0.9));
            this.queueEvent(event);
        } else {
            playerEntity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), "Wired kick exception: " + kickException));
        }
    }
}
