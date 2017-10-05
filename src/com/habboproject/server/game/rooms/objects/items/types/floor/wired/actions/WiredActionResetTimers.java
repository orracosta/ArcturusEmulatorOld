package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerAtGivenTime;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerAtGivenTimeLong;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.List;

public class WiredActionResetTimers extends WiredActionItem {
    public WiredActionResetTimers(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 1;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        final List<WiredTriggerAtGivenTime> items = this.getRoom().getItems().getByClass(WiredTriggerAtGivenTime.class);

        items.addAll(this.getRoom().getItems().getByClass(WiredTriggerAtGivenTimeLong.class));

        for (WiredTriggerAtGivenTime floorItem : items) {
            floorItem.setNeedsReset(false);
        }

        this.getRoom().resetWiredTimer();
    }
}
