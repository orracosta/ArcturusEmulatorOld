package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

/**
 * Created by brend on 03/02/2017.
 */
public class WiredActionMuteTriggerer extends WiredActionItem {
    public static final int PARAM_MUTE_TIME = 0;

    public WiredActionMuteTriggerer(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getWiredData().getParams().size() < 1) {
            this.getWiredData().getParams().clear();
            this.getWiredData().getParams().put(PARAM_MUTE_TIME, 0);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 20;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (event.entity == null || !(event.entity instanceof PlayerEntity)) {
            return;
        }

        int time = this.getWiredData().getParams().get(PARAM_MUTE_TIME);

        String message = this.getWiredData().getText();

        if (time > 0) {
            ((PlayerEntity)event.entity).getPlayer().getSession().send(new WhisperMessageComposer(((PlayerEntity)event.entity).getPlayerId(), "Wired Mute: Silenciado por " + time + (time > 1 ? "minutos" : "minuto") + "! Message: " + (message == null || message.isEmpty() ? "Nenhuma mensagem" : message)));

            if (this.getRoom().getRights().hasMute(((PlayerEntity)event.entity).getPlayerId())) {
                this.getRoom().getRights().updateMute(((PlayerEntity)event.entity).getPlayerId(), time);
            } else {
                this.getRoom().getRights().addMute(((PlayerEntity)event.entity).getPlayerId(), time);
            }
        }
    }
}
