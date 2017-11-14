package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;

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
        RoomTile t = this.room.getLayout().getTile((short) x, (short) y);
        if(this.newEffect == 4)
            this.newEffect = 0;

        HabboItem oldItem = this.room.getTopItemAt(this.roomUnit.getX(), this.roomUnit.getY());
        HabboItem topItem = this.room.getTopItemAt(t.x, t.y);

        this.roomUnit.setGoalLocation(t);
        this.roomUnit.getStatus().remove("mv");
        this.room.sendComposer(new RoomUnitOnRollerComposer(this.roomUnit, null, t, this.room).compose());
        this.room.giveEffect(this.roomUnit, this.newEffect);
        this.room.updateRoomUnit(t.x, t.y, this.roomUnit);

        try {
            topItem.onWalkOn(this.roomUnit, room, new Object[]{oldItem});
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.roomUnit.getPath().clear();
        this.roomUnit.isTeleporting = false;

        if(this.roomUnit.isTeleporting)
            this.roomUnit.isTeleporting = false;
    }
}
