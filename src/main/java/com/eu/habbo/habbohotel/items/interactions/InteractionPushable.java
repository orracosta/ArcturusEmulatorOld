package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Rotation;
import com.eu.habbo.util.pathfinding.Tile;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionPushable extends InteractionDefault
{
    public int velocity;
    public RoomUserRotation direction;
    public int skip;

    public InteractionPushable(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionPushable(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, final Room room, Object[] objects)  throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        this.velocity = 1;

        if (roomUnit.getGoal().equals(new Tile(this.getX(), this.getY())))
        {
            if(roomUnit.tilesWalked() == 1)
            {
                if (PathFinder.tilesAdjecent(roomUnit.getStartLocation(), new Tile(this.getX(), this.getY())))
                {
                    this.velocity = 8;
                }
                else
                {
                    this.velocity = 6;
                }
            }
        }

        this.direction = roomUnit.getBodyRotation();
        this.cycle(room);
    }

    private void slowDown(Room room)
    {
        this.velocity--;

        if (this.velocity == 0)
        {
            this.stop(room);
        }
    }

    protected void stop(Room room)
    {
        this.setExtradata("0");
        room.updateItem(this);
    }

    protected void cycle(final Room room)
    {
        Tile newLocation = new Tile(this.getX(), this.getY());

        int skip = 0;
        if(this.velocity == 6)
        {
            skip = 2;
        }
        else if (this.velocity < 6 && this.velocity >= 3)
        {
            skip = 1;
        }

        for(int i = 0; i <= skip; i++)
        {
            newLocation = PathFinder.getSquareInFront(newLocation.X, newLocation.Y, this.direction.getValue());

            if (room.hasHabbosAt(newLocation.X, newLocation.Y))
            {
                Tile checkLocation = PathFinder.getSquareInFront(newLocation.X, newLocation.Y, this.direction.getValue() + 4 % 8);

                if (room.hasHabbosAt(checkLocation.X, checkLocation.Y))
                {
                    this.stop(room);
                    return;
                }
                else
                {

                }
            }
        }

        newLocation.Z = room.getStackHeight(newLocation.X, newLocation.Y, false);

        this.calcState();
        if (room.getLayout().tileExists(newLocation.X, newLocation.Y) && room.tileWalkable(newLocation))
        {
            room.updateItem(this);
            room.sendComposer(new FloorItemOnRollerComposer(this, null, newLocation, room).compose());
        }
        else
        {
            this.stop(room);
            return;
        }

        if (this.velocity == 1)
        {
            Emulator.getThreading().run(new Runnable()
            {
                @Override
                public void run()
                {
                    slowDown(room);
                }
            }, 500);
        }
    }

    @Override
    public int getRotation()
    {
        if(this.direction == null)
        {
            return super.getRotation();
        }

        return this.direction.getValue();
    }

    protected abstract void calcState();
}
