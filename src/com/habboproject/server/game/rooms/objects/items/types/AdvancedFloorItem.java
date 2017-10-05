package com.habboproject.server.game.rooms.objects.items.types;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.executors.item.FloorItemExecuteEvent;
import com.habboproject.server.utilities.collections.ConcurrentHashSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brend on 29/04/2017.
 */
public abstract class AdvancedFloorItem<T extends FloorItemExecuteEvent> extends RoomItemFloor {
    private final Set<T> itemEvents = new ConcurrentHashSet<T>();

    public AdvancedFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void onTick() {
        final Set<T> finishedEvents = new HashSet<T>();

        for(T itemEvent : itemEvents) {
            itemEvent.incrementTicks();

            if(itemEvent.isFinished()) {
                finishedEvents.add(itemEvent);
            }
        }

        for(T finishedEvent : finishedEvents) {
            this.itemEvents.remove(finishedEvent);

            finishedEvent.onCompletion(this);

            if (finishedEvent.isInteractiveEvent()) {
                this.onEventComplete(finishedEvent);
            }
        }

        finishedEvents.clear();
    }

    public void queueEvent(final T floorItemEvent) {
        if(this.getMaxEvents() <= this.itemEvents.size()) {
            return;
        }

        this.itemEvents.add(floorItemEvent);
    }

    protected abstract void onEventComplete(T event);

    public int getMaxEvents() {
        return 5000;
    }
}
