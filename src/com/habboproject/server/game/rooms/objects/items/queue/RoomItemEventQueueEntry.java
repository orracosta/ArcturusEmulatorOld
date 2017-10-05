package com.habboproject.server.game.rooms.objects.items.queue;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItem;


public class RoomItemEventQueueEntry {
    private final RoomItem item;
    private final RoomEntity entity;

    private final RoomItemEventType type;

    // optional
    private final int requestData;
    private final boolean isWiredTrigger;

    public RoomItemEventQueueEntry(RoomItem item, RoomItemEventType type) {
        this.item = item;
        this.entity = null;

        this.type = type;

        this.requestData = -1;
        this.isWiredTrigger = false;
    }

    public RoomItemEventQueueEntry(RoomItem item, RoomEntity entity, RoomItemEventType type) {
        this.item = item;
        this.entity = entity;

        this.type = type;

        this.requestData = -1;
        this.isWiredTrigger = false;
    }

    public RoomItemEventQueueEntry(RoomItem item, RoomEntity entity, RoomItemEventType type, int requestData, boolean isWiredTrigger) {
        this.item = item;
        this.entity = entity;

        this.type = type;

        this.requestData = requestData;
        this.isWiredTrigger = isWiredTrigger;
    }

    public RoomItem getItem() {
        return item;
    }

    public RoomEntity getEntity() {
        return entity;
    }

    public RoomItemEventType getType() {
        return type;
    }

    public int getRequestData() {
        return requestData;
    }

    public boolean isWiredTrigger() {
        return isWiredTrigger;
    }
}
