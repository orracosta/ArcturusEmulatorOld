package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredHandler;

/**
 * Created on 28-6-2015 14:00.
 */
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
