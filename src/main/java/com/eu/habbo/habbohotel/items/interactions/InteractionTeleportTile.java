package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.teleport.TeleportActionOne;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Murdock on 14/11/2016.
 */
public class InteractionTeleportTile extends InteractionTeleport
{
    public InteractionTeleportTile(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionTeleportTile(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return super.canWalkOn(roomUnit, room, objects);
    }

    @Override
    public boolean isWalkable()
    {
        return super.isWalkable();
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        //super.onClick(client, room, objects);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalk(roomUnit, room, objects);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        Habbo habbo = room.getHabbo(roomUnit);

        if(room != null)
        {
            if (this.canUseTeleport(habbo.getClient(), room))
            {
                habbo.getRoomUnit().isTeleporting = true;
                super.setExtradata("1");
                room.updateItem(this);

                Emulator.getThreading().run(new TeleportActionOne(this, room, habbo.getClient()), 500);
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);
    }

    @Override
    public void run()
    {
        super.run();
    }

    @Override
    public void onPickUp(Room room)
    {
        super.onPickUp(room);
    }

    private boolean canUseTeleport(GameClient client, Room room)
    {
        if(client.getHabbo().getRoomUnit().isTeleporting)
            return false;

        if(!room.getHabbosAt(getX(), getY()).isEmpty())
            return false;

        if(!this.getExtradata().equals("0"))
            return false;

        return true;
    }
}
