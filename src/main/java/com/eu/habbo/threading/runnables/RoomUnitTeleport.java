package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 20-12-2014 13:44.
 */
public class RoomUnitTeleport implements Runnable
{
    private RoomUnit roomUnit;
    private Room room;
    private int x;
    private int y;
    private double z;

    private int newEffect;

    public RoomUnitTeleport(RoomUnit roomUnit, Room room, int x, int y, double z, int newEffect)
    {
        this.roomUnit = roomUnit;
        this.room = room;
        this.x = x;
        this.y = y;
        this.z = z;
        this.newEffect = newEffect;
    }

    @Override
    public void run()
    {
        this.roomUnit.setGoalLocation(x, y);
        this.roomUnit.getStatus().remove("mv");

        Tile t = new Tile(x, y, z);

        this.room.sendComposer(new RoomUnitOnRollerComposer(this.roomUnit, null, t, this.room).compose());
        this.room.giveEffect(this.roomUnit, this.newEffect);
        this.room.updateHabbosAt(this.x, this.y);
    }
}
