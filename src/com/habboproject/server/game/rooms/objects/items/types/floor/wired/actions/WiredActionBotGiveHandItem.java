package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

public class WiredActionBotGiveHandItem extends WiredActionItem {
    private static final int PARAM_HANDITEM = 0;

    public WiredActionBotGiveHandItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
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
        return 24;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (this.getWiredData().getParams().size() != 1) {
            return;
        }

        if (this.getWiredData().getText().isEmpty()) {
            return;
        }

        if (event.entity == null || !(event.entity instanceof PlayerEntity)) {
            return;
        }

        int param = this.getWiredData().getParams().get(PARAM_HANDITEM);

        String botName = this.getWiredData().getText();
        BotEntity botEntity = this.getRoom().getBots().getBotByName(botName);

        if (botEntity != null) {
            if (botEntity.isWalking()) {
                botEntity.cancelWalk();
            }

            if (event.entity.getPosition().getX() != this.getRoom().getModel().getDoorX() && event.entity.getPosition().getY() != this.getRoom().getModel().getDoorY()) {
                double distance = event.entity.getPosition().distanceTo(event.entity.getPosition());

                if (distance != 0.0 && distance <= 1.0) {
                    botEntity.getData().setForcedUserTargetMovement(event.entity.getId());
                    botEntity.getData().setCarryingItemToUser(true);
                    botEntity.carryItem(param);

                    botEntity.moveTo(event.entity.getPosition().getX(), event.entity.getPosition().getY());
                } else {
                    this.getRoom().getEntities().broadcastMessage(new TalkMessageComposer(botEntity.getId(), Locale.get("bots.chat.giveItemMessage").replace("%username%", event.entity.getUsername()), RoomManager.getInstance().getEmotions().getEmotion(":)"), 2));

                    event.entity.carryItem(param);
                }
            }
        }
    }
}
