package com.habboproject.server.threads.executors.item;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by brend on 29/04/2017.
 */
public abstract class FloorItemExecuteEvent {
    private final AtomicInteger ticks;
    private int totalTicks;

    protected FloorItemExecuteEvent(int totalTicks) {
        this.ticks = new AtomicInteger(0);
        this.totalTicks = totalTicks;
    }

    /** You can override this to FORCE a callback! (even if the onEventComplete method is overriden) */
    public void onCompletion(final RoomItemFloor floorItem) {

    }

    public void setTotalTicks(final int ticks) {
        this.ticks.set(0);
        this.totalTicks = ticks;
    }

    public void incrementTicks() {
        this.ticks.incrementAndGet();
    }

    public boolean isFinished() {
        return this.ticks.get() >= this.totalTicks;
    }

    public boolean isInteractiveEvent() {
        return true;
    }
}
