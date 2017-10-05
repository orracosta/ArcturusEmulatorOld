package com.habboproject.server.threads.executors.wired.events;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.threads.executors.item.FloorItemExecuteEvent;

/**
 * Created by brend on 29/04/2017.
 */
public class WiredItemExecuteEvent extends FloorItemExecuteEvent {
    public int type = 0;
    public RoomEntity entity;
    public Object data;

    public WiredItemExecuteEvent(RoomEntity entity, Object data) {
        super(0);

        this.entity = entity;
        this.data = data;
    }

    @Override
    public void onCompletion(RoomItemFloor floor) {
        if(floor instanceof WiredActionItem) {
            ((WiredFloorItem) floor).flash();
        }
    }
}
