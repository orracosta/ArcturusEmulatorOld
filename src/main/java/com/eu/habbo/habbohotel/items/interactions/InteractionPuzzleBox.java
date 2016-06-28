package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPuzzleBox extends HabboItem
{
    public InteractionPuzzleBox(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionPuzzleBox(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(client.getHabbo().getRoomUnit().getStatus().containsKey("mv"))
            return;

        if(!PathFinder.tilesAdjecent(super.getX(), super.getY(), client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY()))
            return;


        Tile boxLocation = new Tile(this.getX(), this.getY(), this.getZ());
        client.getHabbo().getRoomUnit().lookAtPoint(boxLocation);
        room.sendComposer(new RoomUserStatusComposer(client.getHabbo().getRoomUnit()).compose());

        switch (client.getHabbo().getRoomUnit().getBodyRotation())
        {
            case NORTH_EAST:
            case NORTH_WEST:
            case SOUTH_EAST:
            case SOUTH_WEST:
                return;
        }

        Tile tile = PathFinder.getSquareInFront(super.getX(), super.getY(), client.getHabbo().getRoomUnit().getBodyRotation().getValue());

        if (!room.tileWalkable(tile))
        {
            return;
        }

        if(!boxLocation.equals(PathFinder.getSquareInFront(client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY(), client.getHabbo().getRoomUnit().getBodyRotation().getValue())))
            return;

        HabboItem item = room.getTopItemAt(tile.X, tile.Y);

        if(item == null || (item.getZ() <= this.getZ() && item.getBaseItem().allowWalk()))
        {
            room.sendComposer(new FloorItemOnRollerComposer(this, null, tile, room).compose());
            client.getHabbo().getRoomUnit().setGoalLocation(boxLocation);
            this.needsUpdate(true);
        }
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt32((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return false;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }
}
