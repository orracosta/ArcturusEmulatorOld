package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.util.List;

public class RoomUnitWalkToRoomUnit implements Runnable
{
    private RoomUnit walker;
    private RoomUnit target;
    private Room room;
    private List<Runnable> targetReached;
    private List<Runnable> failedReached;

    private RoomTile goalTile = null;

    public RoomUnitWalkToRoomUnit(RoomUnit walker, RoomUnit target, Room room, List<Runnable> targetReached, List<Runnable> failedReached)
    {
        this.walker = walker;
        this.target = target;
        this.room = room;
        this.targetReached = targetReached;
        this.failedReached = failedReached;
    }

    @Override
    public void run()
    {
        if(this.goalTile == null)
        {
            this.findNewLocation();
            Emulator.getThreading().run(this, 500);
        }
        else if(this.walker.getGoal().equals(this.goalTile)) //Check if the goal is still the same. Chances are something is running the same task. If so we dump this task.
        {
            //Check if arrived.
            if(this.walker.getCurrentLocation().equals(this.goalTile))
            {
                for(Runnable r : this.targetReached)
                {
                    Emulator.getThreading().run(r);

                    WiredHandler.handle(WiredTriggerType.BOT_REACHED_AVTR, this.target, this.room, new Object[]{this.walker});
                }
            }
            else
            {
                List<RoomTile> tiles = room.getLayout().getTilesAround(this.target.getCurrentLocation());

                for(RoomTile t : tiles)
                {
                    if(t.equals(this.goalTile))
                    {
                        Emulator.getThreading().run(this, 500);
                        return;
                    }
                }

                this.findNewLocation();

                Emulator.getThreading().run(this, 500);
            }
        }
    }

    private void findNewLocation()
    {
        this.goalTile = this.room.getLayout().getTileInFront(this.target.getCurrentLocation(), this.target.getBodyRotation().getValue());

        if (this.goalTile == null)
            return;

        if (!this.room.tileWalkable(this.goalTile))
        {
            List<RoomTile> tiles = room.getLayout().getTilesAround(this.target.getCurrentLocation());

            for (RoomTile t : tiles)
            {
                if (this.room.tileWalkable(t))
                {
                    this.goalTile = t;

                    break;
                }
            }
        }

        this.walker.setGoalLocation(this.goalTile);

        if(this.walker.getPath() == null)
        {
            for(Runnable r : this.failedReached)
            {
                Emulator.getThreading().run(r);
            }
        }
    }
}
