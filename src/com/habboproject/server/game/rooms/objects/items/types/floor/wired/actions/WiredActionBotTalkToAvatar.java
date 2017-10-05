package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.network.messages.outgoing.room.avatar.ShoutMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

/**
 * Created by brend on 02/02/2017.
 */
public class WiredActionBotTalkToAvatar extends WiredActionItem {
    public static final int PARAM_MESSAGE_TYPE = 0;

    public WiredActionBotTalkToAvatar(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getWiredData().getParams().size() < 1) {
            this.getWiredData().getParams().clear();
            this.getWiredData().getParams().put(0, 0);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 27;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (event.entity == null || !(event.entity instanceof PlayerEntity)) {
            return;
        }

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

        message = message.replace("{username}", event.entity.getUsername()).replace("%username%", event.entity.getUsername()).replace("<", "").replace(">", "");

        BotEntity botEntity = this.getRoom().getBots().getBotByName(botName);
        if (botEntity != null) {
            boolean isShout = this.getWiredData().getParams().size() == 1 && this.getWiredData().getParams().get(PARAM_MESSAGE_TYPE) == 1;

            if (isShout) {
                ((PlayerEntity)event.entity).getPlayer().getSession().send(new WhisperMessageComposer(botEntity.getId(), message, 2));
            } else {
                ((PlayerEntity)event.entity).getPlayer().getSession().send(new ShoutMessageComposer(botEntity.getId(), message, ChatEmotion.NONE, 2));
            }
        }
    }
}
