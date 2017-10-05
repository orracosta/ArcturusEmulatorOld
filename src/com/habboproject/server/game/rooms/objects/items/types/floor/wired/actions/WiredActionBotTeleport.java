package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.wired.actions.ActionBotTeleportExecuteEvent;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.concurrent.ExecutionException;

/**
 * Created by brend on 02/02/2017.
 */
public class WiredActionBotTeleport extends WiredActionItem {
    public WiredActionBotTeleport(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 21;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        try {
            ThreadManager.getInstance().execute(new ActionBotTeleportExecuteEvent(this, this.getWiredData().getSelectedIds()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
