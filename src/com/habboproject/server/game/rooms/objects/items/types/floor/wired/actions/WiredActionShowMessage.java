package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.concurrent.TimeUnit;


public class WiredActionShowMessage extends WiredActionItem {
    protected boolean isWhisperBubble = false;

    public WiredActionShowMessage(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
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

        PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        if(playerEntity.getPlayer() == null || playerEntity.getPlayer().getSession() == null) {
            return;
        }

        if(this.getWiredData() == null || this.getWiredData().getText() == null) {
            return;
        }

        playerEntity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), this.getWiredData().getText(), isWhisperBubble ? 0 : 34));
    }
}
