package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.ItemStateComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionWired extends HabboItem
{
    private long cooldown;

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

    public abstract void serializeWiredData(ServerMessage message, Room room);

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
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
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

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE items SET wired_data = ? WHERE id = ?"))
            {
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
            }
            catch (SQLException e)
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

    public void activateBox(Room room)
    {
        this.setExtradata(this.getExtradata().equals("1") ? "0" : "1");
        room.sendComposer(new ItemStateComposer(this).compose());
    }

    /**
     * @return The delay between two activations.
     */
    protected long requiredCooldown()
    {
        return 0;
    }


    /**
     * Checks if the cooldown has passed and updates it to the new cooldown.
     * @param newMillis The new timestamp the wired was executed.
     * @return True if the wired can be executed.
     */
    public boolean canExecute(long newMillis)
    {
        if (newMillis - this.cooldown < this.requiredCooldown())
        {
            this.cooldown = newMillis;
            return false;
        }

        this.cooldown = newMillis;
        return true;
    }
}
