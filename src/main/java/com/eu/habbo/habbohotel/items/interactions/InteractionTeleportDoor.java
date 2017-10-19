package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.unknown.ItemStateComposer2;
import com.eu.habbo.threading.runnables.teleport.TeleportActionOne;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTeleportDoor extends InteractionTeleport
{
    private int roomUnitID = 0;
    public InteractionTeleportDoor(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionTeleportDoor(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(room != null && client != null && objects.length <= 1)
        {
            RoomTile teleportPosition = room.getLayout().getTile(getX(), getY());

            if (teleportPosition != null && !teleportPosition.equals(client.getHabbo().getRoomUnit().getCurrentLocation()))
            {
                client.getHabbo().getRoomUnit().setGoalLocation(teleportPosition);
            }
            else if (teleportPosition.equals(client.getHabbo().getRoomUnit().getCurrentLocation()))
            {
                startTeleport(room, client.getHabbo());
            }
        }
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {}

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {}

    private boolean canUseTeleport(GameClient client, Room room)
    {
        if(!this.getExtradata().equals("0"))
            return false;

        if(client.getHabbo().getRoomUnit().isTeleporting)
            return false;

        if (client.getHabbo().getRoomUnit().getCurrentLocation().is(this.getX(), this.getY()))
            return true;

        return true;
    }

    public void startTeleport(Room room, Habbo habbo)
    {
        if (this.canUseTeleport(habbo.getClient(), room))
        {
            this.roomUnitID = 0;
            habbo.getRoomUnit().isTeleporting = true;
            room.scheduledTasks.add(new TeleportActionOne(this, room, habbo.getClient()));
        }
    }
}
