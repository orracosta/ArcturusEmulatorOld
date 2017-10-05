package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

/**
 * Created by brend on 02/02/2017.
 */
public class WiredActionBotClothes extends WiredActionItem {
    public WiredActionBotClothes(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 26;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (event.entity == null || !(event.entity instanceof PlayerEntity)) {
            return;
        }

        if (!this.getWiredData().getText().contains("\t")) {
            return;
        }

        String[] packetData = this.getWiredData().getText().split("\t");
        if (packetData.length != 2) {
            return;
        }

        String botName = packetData[0];
        String botLook = packetData[1];

        if (botName.isEmpty() || botLook.isEmpty()) {
            return;
        }

        BotEntity botEntity = this.getRoom().getBots().getBotByName(botName);
        if (botEntity != null) {
            botEntity.getData().setFigure(botLook);
            botEntity.getData().setGender("M");

            this.getRoom().getEntities().broadcastMessage(new UpdateInfoMessageComposer(botEntity));

            botEntity.getData().save();
        }
    }
}