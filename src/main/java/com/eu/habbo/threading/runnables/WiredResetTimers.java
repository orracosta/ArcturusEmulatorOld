package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredHandler;

public class WiredResetTimers implements Runnable
{
    private Room room;

    public WiredResetTimers(Room room)
    {
        this.room = room;
    }

    @Override
    public void run()
    {
        WiredHandler.resetTimers(this.room);
    }
}
