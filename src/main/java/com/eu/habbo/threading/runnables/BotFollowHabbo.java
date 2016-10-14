package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.util.pathfinding.PathFinder;

public class BotFollowHabbo implements Runnable
{
    private Bot bot;
    private Habbo habbo;
    private Room room;

    public BotFollowHabbo(Bot bot, Habbo habbo, Room room)
    {
        this.bot = bot;
        this.habbo = habbo;
        this.room = room;
    }

    @Override
    public void run()
    {
        if (this.bot != null)
        {
            if (this.habbo != null && this.bot.getFollowingHabboId() == this.habbo.getHabboInfo().getId())
            {
                if(this.habbo.getHabboInfo().getCurrentRoom() != null && this.habbo.getHabboInfo().getCurrentRoom() == this.room)
                {
                    if (this.habbo.getRoomUnit() != null)
                    {
                        if (this.bot.getRoomUnit() != null)
                        {
                            RoomTile target = PathFinder.getSquareInFront(this.room.getLayout(), this.habbo.getRoomUnit().getX(), this.habbo.getRoomUnit().getY(), Math.abs((this.habbo.getRoomUnit().getBodyRotation().getValue() + 4)) % 8);

                            if (target.x < 0 || target.y < 0)
                                target = PathFinder.getSquareInFront(this.room.getLayout(), this.habbo.getRoomUnit().getX(), this.habbo.getRoomUnit().getY(), this.habbo.getRoomUnit().getBodyRotation().getValue());

                            if (target.x >= 0 && target.y >= 0)
                            {
                                this.bot.getRoomUnit().setGoalLocation(target);
                                this.bot.getRoomUnit().setCanWalk(true);
                                Emulator.getThreading().run(this, 500);
                            }
                        }
                    }
                }
            }
        }
    }
}
