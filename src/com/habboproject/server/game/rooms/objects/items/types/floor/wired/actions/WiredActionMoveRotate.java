package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.wired.actions.ActionMoveRotateExecuteEvent;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;


public class WiredActionMoveRotate extends WiredActionItem {
    public WiredActionMoveRotate(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 4;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        ThreadManager.getInstance().execute(new ActionMoveRotateExecuteEvent(this, this.getWiredData().getSelectedIds()));
    }
}
