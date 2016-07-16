package com.eu.habbo.habbohotel.guilds;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Guild implements Runnable
{
    private int id;
    private int ownerId;
    private String name;
    private String description;
    private int roomId;
    private GuildState state;
    private int rights;
    private int colorOne;
    private int colorTwo;
    private String badge;
    private int dateCreated;

    private int memberCount;
    private int requestCount;

    public boolean needsUpdate;
    public int lastRequested = Emulator.getIntUnixTimestamp();

    public Guild(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.ownerId = set.getInt("user_id");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.state = GuildState.values()[set.getInt("state")];
        this.roomId = set.getInt("room_id");
        this.rights = set.getString("rights").equalsIgnoreCase("1") ? 1 : 0;
        this.colorOne = set.getInt("color_one");
        this.colorTwo = set.getInt("color_two");
        this.badge = set.getString("badge");
        this.dateCreated = set.getInt("date_created");
        this.memberCount = 0;
        this.requestCount = 0;
    }

    public Guild(int ownerId, int roomId, String name, String description, int colorOne, int colorTwo, String badge)
    {
        this.id = 0;
        this.ownerId = ownerId;
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.state = GuildState.OPEN;
        this.rights = 0;
        this.colorOne = colorOne;
        this.colorTwo = colorTwo;
        this.badge = badge;
        this.memberCount = 0;
    }

    public void loadMemberCount()
    {
        try
        {

            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT COUNT(id) as count FROM guilds_members WHERE level_id < 3 AND guild_id = ?");
            statement.setInt(1, this.id);
            ResultSet set = statement.executeQuery();

            if(set.next())
            {
                this.memberCount = set.getInt(1);
            }

            set.close();
            statement.close();
            statement.getConnection().close();

            statement = Emulator.getDatabase().prepare("SELECT COUNT(id) as count FROM guilds_members WHERE level_id = 3 AND guild_id = ?");
            statement.setInt(1, this.id);
            set = statement.executeQuery();

            if(set.next())
            {
                this.requestCount = set.getInt(1);
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE guilds SET name = ?, description = ?, state = ?, rights = ?, color_one = ?, color_two = ?, badge = ? WHERE id = ?");
                statement.setString(1, this.name);
                statement.setString(2, this.description);
                statement.setInt(3, this.state.state);
                statement.setString(4, this.rights == 1 ? "1" : "0");
                statement.setInt(5, this.colorOne);
                statement.setInt(6, this.colorTwo);
                statement.setString(7, this.badge);
                statement.setInt(8, this.id);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
            this.needsUpdate = false;
        }
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getRoomId()
    {
        return this.roomId;
    }

    public GuildState getState()
    {
        return this.state;
    }

    public void setState(GuildState state)
    {
        this.state = state;
    }

    public int getRights()
    {
        return this.rights;
    }

    public void setRights(int rights)
    {
        this.rights = rights;
    }

    public int getColorOne()
    {
        return this.colorOne;
    }

    public void setColorOne(int colorOne)
    {
        this.colorOne = colorOne;
    }

    public int getColorTwo()
    {
        return this.colorTwo;
    }

    public void setColorTwo(int colorTwo)
    {
        this.colorTwo = colorTwo;
    }

    public String getBadge()
    {
        return this.badge;
    }

    public void setBadge(String badge)
    {
        this.badge = badge;
    }

    public int getOwnerId()
    {
        return this.ownerId;
    }

    public int getDateCreated()
    {
        return dateCreated;
    }

    public int getMemberCount()
    {
        return this.memberCount;
    }

    public void increaseMemberCount()
    {
        this.memberCount++;
    }

    public void decreaseMemberCount()
    {
        this.memberCount--;
    }

    public int getRequestCount()
    {
        return this.requestCount;
    }

    public void increaseRequestCount()
    {
        this.requestCount++;
    }

    public void decreaseRequestCount()
    {
        this.requestCount--;
    }

    public boolean hasGuild()
    {
        return true;
    }
}
