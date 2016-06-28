package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.IEventTriggers;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class HabboItem implements Runnable, IEventTriggers
{
    private int id;
    private int userId;
    private int roomId;
    private Item baseItem;
    private String wallPosition;
    private int x;
    private int y;
    private double z;
    private int rotation;
    private String extradata;
    private int limitedStack;
    private int limitedSells;
    private boolean needsUpdate = false;
    private boolean needsDelete = false;

    public HabboItem(ResultSet set, Item baseItem) throws SQLException
    {
        this.id = set.getInt("id");
        this.userId = set.getInt("user_id");
        this.roomId = set.getInt("room_id");
        this.baseItem = baseItem;
        this.wallPosition = set.getString("wall_pos");
        this.x = set.getInt("x");
        this.y = set.getInt("y");
        this.z = set.getDouble("z");
        this.rotation = set.getInt("rot");
        this.extradata = set.getString("extra_data");
        this.limitedStack = Integer.parseInt(set.getString("limited_data").split(":")[0]);
        this.limitedSells = Integer.parseInt(set.getString("limited_data").split(":")[1]);
    }

    public HabboItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        this.id = id;
        this.userId = userId;
        this.roomId = 0;
        this.baseItem = item;
        this.wallPosition = "";
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.rotation = 0;
        this.extradata = extradata;
        this.limitedSells = limitedSells;
        this.limitedStack = limitedStack;
    }

    public void serializeFloorData(ServerMessage serverMessage)
    {
        try
        {
            serverMessage.appendInt32(this.getId());
            serverMessage.appendInt32(this.baseItem.getSpriteId());
            serverMessage.appendInt32(this.x);
            serverMessage.appendInt32(this.y);
            serverMessage.appendInt32(getRotation());
            serverMessage.appendString(Double.toString(this.z));

            serverMessage.appendString((this.getBaseItem().getInteractionType().getType() == InteractionTrophy.class || this.getBaseItem().getInteractionType().getType() == InteractionCrackable.class || this.getBaseItem().getName().toLowerCase().equals("gnome_box")) ? "1.0" : ((this.getBaseItem().allowWalk() || this.getBaseItem().allowSit() && this.roomId != 0) ? Item.getCurrentHeight(this) + "" : ""));
            //serverMessage.appendString( ? "1.0" : ((this.getBaseItem().allowWalk() || this.getBaseItem().allowSit() && this.roomId != 0) ? Item.getCurrentHeight(this) : ""));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void serializeExtradata(ServerMessage serverMessage)
    {
        if(this.isLimited())
        {
            serverMessage.appendInt32(this.getLimitedSells());
            serverMessage.appendInt32(this.getLimitedStack());
        }
    }

    public void serializeWallData(ServerMessage serverMessage)
    {
        serverMessage.appendString(this.getId() + "");
        serverMessage.appendInt32(this.baseItem.getSpriteId());
        serverMessage.appendString(this.wallPosition);

        if(this instanceof InteractionPostIt)
            serverMessage.appendString(this.extradata.split(" ")[0]);
        else
            serverMessage.appendString(this.extradata);
        serverMessage.appendInt32(this.getUserId());
        serverMessage.appendInt32(this.getBaseItem().getStateCount() > 1 || this instanceof InteractionCrackable || this instanceof InteractionMultiHeight ? 1 : 0);
        serverMessage.appendInt32(0);
    }

    public int getId()
    {
        return this.id;
    }

    public int getUserId()
    {
        return this.userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public int getRoomId()
    {
        return this.roomId;
    }

    public void setRoomId(int roomId)
    {
        this.roomId = roomId;
    }

    public Item getBaseItem()
    {
        return this.baseItem;
    }

    public String getWallPosition()
    {
        return wallPosition;
    }

    public void setWallPosition(String wallPosition)
    {
        this.wallPosition = wallPosition;
    }

    public int getX()
    {
        return this.x;
    }

    public void setX(int x)
    {
        this.x = (short)x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setY(int y)
    {
        this.y = (short)y;
    }

    public double getZ()
    {
        return this.z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public Tile getLocation()
    {
        return new Tile(this.x, this.y, this.z);
    }

    public int getRotation()
    {
        return this.rotation;
    }

    public void setRotation(int rotation)
    {
        this.rotation = (byte)(rotation % 8);
    }

    public String getExtradata()
    {
        return this.extradata;
    }

    public void setExtradata(String extradata)
    {
        this.extradata = extradata;
    }

    public boolean needsUpdate()
    {
        return this.needsUpdate;
    }

    public void needsUpdate(boolean value)
    {
        this.needsUpdate = value;
    }

    public void needsDelete(boolean value)
    {
        this.needsDelete = value;
    }

    public boolean isLimited()
    {
        return this.limitedStack > 0;
    }

    public int getLimitedStack()
    {
        return limitedStack;
    }

    public int getLimitedSells()
    {
        return limitedSells;
    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            PreparedStatement statement = null;
            try
            {
                statement = Emulator.getDatabase().prepare("UPDATE items SET user_id = ?, room_id = ?, wall_pos = ?, x = ?, y = ?, z = ?, rot = ?, extra_data = ?, limited_data = ? WHERE id = ?");
                statement.setInt(1, this.userId);
                statement.setInt(2, this.roomId);
                statement.setString(3, this.wallPosition);
                statement.setInt(4, this.x);
                statement.setInt(5, this.y);
                statement.setDouble(6, this.z);
                statement.setInt(7, this.rotation);
                statement.setString(8, this instanceof InteractionGuildGate ? "" : this.getExtradata());
                statement.setString(9, this.limitedStack + ":" + this.limitedSells);
                statement.setInt(10, this.id);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
            finally
            {
                try
                {
                    if (statement != null)
                    {
                        statement.close();
                        statement.getConnection().close();
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }

            this.needsUpdate = false;
        }
        if(this.needsDelete)
        {
            this.needsUpdate = false;
            this.needsDelete = false;
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM items WHERE id = ?");
                statement.setInt(1, this.getId());
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public static Tile getSquareInFront(HabboItem item)
    {
        return PathFinder.getSquareInFront(item.getX(), item.getY(), item.getRotation());
    }

    public abstract boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects);

    public abstract boolean isWalkable();

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(client != null && this.getBaseItem().getType().equalsIgnoreCase("s"))
            WiredHandler.handle(WiredTriggerType.STATE_CHANGED, client.getHabbo().getRoomUnit(), room, new Object[]{this});
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        WiredHandler.handle(WiredTriggerType.WALKS_ON_FURNI, roomUnit, room, new Object[]{this});
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        WiredHandler.handle(WiredTriggerType.WALKS_OFF_FURNI, roomUnit, room, new Object[]{this});
    }

    public abstract void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception;


    public void onPlace()
    {
    }

    public void onPickUp()
    {

    }

    public void onMove(Room room, Tile oldLocation, Tile newLocation)
    {

    }
}
