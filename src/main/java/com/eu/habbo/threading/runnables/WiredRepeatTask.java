package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredHandler;

public class WiredRepeatTask implements Runnable
{
    private final InteractionWiredTrigger repeater;
    private final Room room;

    public WiredRepeatTask(InteractionWiredTrigger trigger, Room room)
    {
        this.repeater = trigger;
        this.room = room;
    }

    @Override
    public void run()
    {
        if(this.room != null && this.room.getId() == this.repeater.getRoomId() && this.room.isLoaded())
        {
            WiredHandler.handle(this.repeater, null, this.room, null);
        }
    }
}
