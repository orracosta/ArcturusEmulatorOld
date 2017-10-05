package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.wired.actions.ActionCallStacksExecuteEvent;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;

/**
 * Created by brend on 02/02/2017.
 */
public class WiredActionCallStacks extends WiredActionItem {
    public WiredActionCallStacks(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 18;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        try {
            ThreadManager.getInstance().execute(new ActionCallStacksExecuteEvent(this, this.getWiredData().getSelectedIds(),
                    event.entity, event.data));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
