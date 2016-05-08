package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 12-10-2014 12:53.
 */
public class RoomUnitVendingMachineAction implements Runnable
{
    private final Habbo habbo;
    private final HabboItem habboItem;
    private final Room room;

    public RoomUnitVendingMachineAction(Habbo habbo, HabboItem habboItem, Room room)
    {
        this.habbo = habbo;
        this.habboItem = habboItem;
        this.room = room;
    }

    @Override
    public void run()
    {
        if(this.habbo.getHabboInfo().getCurrentRoom() == room)
        {
            if(this.habboItem.getRoomId() == room.getId())
            {
                Tile tile = HabboItem.getSquareInFront(habboItem);
                if(this.habbo.getRoomUnit().getGoalX() == tile.X && this.habbo.getRoomUnit().getGoalY() == tile.Y)
                {
                    if (tile.X == habbo.getRoomUnit().getX() && tile.Y == habbo.getRoomUnit().getY())
                    {
                        try
                        {
                            this.habboItem.onClick(this.habbo.getClient(), this.room, new Object[]{0});
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        if(room.getGameMap().getNode(tile.X, tile.Y).isWalkable())
                        {
                            this.habbo.getRoomUnit().setGoalLocation(tile.X, tile.Y);
                            Emulator.getThreading().run(this, this.habbo.getRoomUnit().getPathFinder().getPath().size() + 2 * 510);
                        }
                    }
                }
            }
        }
    }
}
