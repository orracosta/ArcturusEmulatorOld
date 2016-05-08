package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.OneWayGateActionOne;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 10-3-2015 09:34.
 */
public class InteractionOneWayGate extends HabboItem
{
    public InteractionOneWayGate(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionOneWayGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return isWalkable() && room.getHabbosAt(this.getX(), this.getY()).isEmpty();
    }

    @Override
    public boolean isWalkable()
    {
        return this.getExtradata().equals("0");
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        if(this.getExtradata().length() == 0)
        {
            this.setExtradata("0");
            this.needsUpdate(true);
        }

        serverMessage.appendInt32((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        Tile tile = PathFinder.getSquareInFront(this.getX(), this.getY(), this.getRotation());

        if(tile.equals(new Tile(client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY(), 0)))
        {
            if(room.getHabbosAt(this.getX(), this.getY()).isEmpty())
            {
                client.getHabbo().getRoomUnit().isTeleporting = true;

                client.getHabbo().getRoomUnit().setGoalLocation(this.getX(), this.getY());
                client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[(this.getRotation() + 4) % 8]);
                client.getHabbo().getRoomUnit().getStatus().put("mv", this.getX() + "," + this.getY() + "," + this.getZ());
                room.sendComposer(new RoomUserStatusComposer(client.getHabbo().getRoomUnit()).compose());
                client.getHabbo().getRoomUnit().setX(this.getX());
                client.getHabbo().getRoomUnit().setY(this.getY());
                client.getHabbo().getRoomUnit().setZ(this.getZ());
                client.getHabbo().getRoomUnit().getStatus().remove("mv");

                this.setExtradata("1");
                room.updateItem(this);

                Emulator.getThreading().run(new OneWayGateActionOne(client, room, this), 300);
            }
        }
    }
}
