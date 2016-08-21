package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.IWired;
import com.eu.habbo.messages.ServerMessage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionWired extends HabboItem implements IWired
{
    InteractionWired(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    InteractionWired(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public abstract boolean execute(RoomUnit roomUnit, Room room, Object[] stuff);

    protected abstract String getWiredData();

    public abstract void loadWiredData(ResultSet set, Room room) throws SQLException;

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);
    }


    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt32((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void run()
    {
        if(this.needsUpdate())
        {
            String wiredData = this.getWiredData();

            if (wiredData == null)
            {
                wiredData = "";
            }

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE items SET wired_data = ? WHERE id = ?");
                if(this.getRoomId() != 0)
                {
                    statement.setString(1, wiredData);
                }
                else
                {
                    statement.setString(1, "");
                }
                statement.setInt(2, this.getId());
                statement.execute();
                statement.close();
                statement.getConnection().close();
            } catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
        super.run();
    }

    @Override
    public void onPickUp(Room room)
    {
        onPickUp();
    }

    public abstract void onPickUp();
}
