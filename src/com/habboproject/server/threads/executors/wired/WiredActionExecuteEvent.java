package com.habboproject.server.threads.executors.wired;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;

import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class WiredActionExecuteEvent implements Callable<Boolean> {
    private final WiredActionItem actionItem;
    private final RoomEntity entity;
    private final Object data;

    public WiredActionExecuteEvent(WiredActionItem actionItem, RoomEntity entity, Object data) {
        this.actionItem = actionItem;
        this.entity = entity;
        this.data = data;
    }

    @Override
    public Boolean call() throws Exception {
        return actionItem.evaluate(entity, data);
    }
}
