package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

/**
 * Created by brend on 02/02/2017.
 */
public class WiredActionBotFollowAvatar extends WiredActionItem {
    private static final int PARAM_STOPFOLLOWING = 0;
    private static final int PARAM_DELAY = 1;

    public WiredActionBotFollowAvatar(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getWiredData().getParams().size() < 2) {
            this.getWiredData().getParams().clear();
            this.getWiredData().getParams().put(0, 1);
            this.getWiredData().getParams().put(1, 0);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 25;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (this.getWiredData().getText().isEmpty()) {
            return;
        }

        if (this.getWiredData().getParams() == null || this.getWiredData().getParams().size() == 0) {
            return;
        }

        String botName = this.getWiredData().getText();
        BotEntity botEntity = this.getRoom().getBots().getBotByName(botName);

        boolean stopFollowing = this.getWiredData().getParams().get(0) == 0;
        boolean hasDelay = this.getWiredData().getParams().get(1) != 0;

        if (botEntity != null) {
            if (stopFollowing) {
                if (botEntity.isWalking()) {
                    botEntity.cancelWalk();
                }

                botEntity.getData().setForcedUserTargetMovement(0);
            } else {
                if (botEntity.isWalking()) {
                    botEntity.cancelWalk();
                }

                if (event.entity.getPosition().getX() != this.getRoom().getModel().getDoorX() && event.entity.getPosition().getY() != this.getRoom().getModel().getDoorY()) {
                    botEntity.getData().setForcedUserTargetMovement(event.entity.getId());
                    botEntity.moveTo(event.entity.getPosition().getX(), event.entity.getPosition().getY());
                }
            }
        }
    }
}
