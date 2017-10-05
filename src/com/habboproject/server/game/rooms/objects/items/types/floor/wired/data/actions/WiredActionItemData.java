package com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.actions;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.WiredItemData;

import java.util.List;
import java.util.Map;


public class WiredActionItemData extends WiredItemData {
    private int delay;

    public WiredActionItemData(int selectionType, List<Long> selectedIds, String text, Map<Integer, Integer> params, Map<Long, WiredItemSnapshot> snapshots, int delay) {
        super(selectionType, selectedIds, text, params, snapshots);
        this.delay = delay;
    }

    public WiredActionItemData(int delay) {
        this.delay = delay;
    }

    public WiredActionItemData() {
        super();
        this.delay = 0;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}