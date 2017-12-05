package com.eu.habbo.habbohotel.items.interactions.games.freeze;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFreezeTile extends HabboItem
{
    public InteractionFreezeTile(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionFreezeTile(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
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
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if (client == null)
            return;

        if (client.getHabbo().getRoomUnit().getCurrentLocation().x == this.getX() && client.getHabbo().getRoomUnit().getCurrentLocation().y == this.getY())
        {
            FreezeGame game = (FreezeGame) room.getGame(FreezeGame.class);

            if (game != null)
                game.throwBall(client.getHabbo(), this);
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onPickUp(Room room)
    {
        this.setExtradata("0");
    }
}
