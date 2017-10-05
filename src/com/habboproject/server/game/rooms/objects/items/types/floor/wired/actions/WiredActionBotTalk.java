package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.network.messages.outgoing.room.avatar.ShoutMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

public class WiredActionBotTalk extends WiredActionItem {
    public static final int PARAM_MESSAGE_TYPE = 0;

    public WiredActionBotTalk(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getWiredData().getParams().size() < 1) {
            this.getWiredData().getParams().clear();
            this.getWiredData().getParams().put(0, 0);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 23;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (!this.getWiredData().getText().contains("\t")) {
            return;
        }

        String[] talkData = this.getWiredData().getText().split("\t");
        if (talkData.length != 2) {
            return;
        }

        String botName = talkData[0];
        String message = talkData[1];

        if (botName.isEmpty() || message.isEmpty()) {
            return;
        }

        if (event.entity instanceof PlayerEntity) {
            message = message.replace("%username%", event.entity.getUsername());
            message = message.replace("{username}", event.entity.getUsername());
        }

        message = message.replace("<", "").replace(">", "");

        BotEntity botEntity = this.getRoom().getBots().getBotByName(botName);
        if (botEntity != null) {
            boolean isShout = this.getWiredData().getParams().size() == 1 && this.getWiredData().getParams().get(0) == 1;

            if (isShout) {
                this.getRoom().getEntities().broadcastMessage(new ShoutMessageComposer(botEntity.getId(), message, ChatEmotion.NONE, 2));
            } else {
                this.getRoom().getEntities().broadcastMessage(new TalkMessageComposer(botEntity.getId(), message, ChatEmotion.NONE, 2));
            }
        }
    }
}
