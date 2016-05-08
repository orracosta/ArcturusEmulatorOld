package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HabboBadge implements Runnable
{
    private int id;
    private String code;
    private int slot;
    private Habbo habbo;
    private boolean needsUpdate;
    private boolean needsInsert;

    public HabboBadge(ResultSet set, Habbo habbo) throws SQLException
    {
        this.id = set.getInt("id");
        this.code = set.getString("badge_code");
        this.slot = set.getInt("slot_id");
        this.habbo = habbo;
        this.needsUpdate = false;
        this.needsInsert = false;
    }

    public HabboBadge(int id, String code, int slot, Habbo habbo)
    {
        this.id = id;
        this.code = code;
        this.slot = slot;
        this.habbo = habbo;
        this.needsUpdate = false;
        this.needsInsert = true;
    }

    public int getId()
    {
        return id;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public void setSlot(int slot)
    {
        this.slot = slot;
    }

    public int getSlot()
    {
        return slot;
    }

    @Override
    public void run()
    {
        try
        {
            if(this.needsInsert)
            {

                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO users_badges (user_id, slot_id, badge_code) VALUES (?, ?, ?)");
                statement.setInt(1, this.habbo.getHabboInfo().getId());
                statement.setInt(2, this.slot);
                statement.setString(3, this.code);
                statement.execute();
                ResultSet set = statement.getGeneratedKeys();
                if(set.next())
                {
                    this.id = set.getInt(1);
                }
                set.close();
                statement.close();
                statement.getConnection().close();
                this.needsInsert = false;
            }

            if(this.needsUpdate)
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users_badges SET slot_id = ?, badge_code = ? WHERE id = ? AND user_id = ?");
                statement.setInt(1, this.slot);
                statement.setString(2, this.code);
                statement.setInt(3, this.id);
                statement.setInt(4, this.habbo.getHabboInfo().getId());
                statement.execute();
                statement.close();
                statement.getConnection().close();
                this.needsUpdate = false;
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void needsUpdate(boolean needsUpdate)
    {
        this.needsUpdate = needsUpdate;
    }

    public void needsInsert(boolean needsInsert)
    {
        this.needsInsert = needsInsert;
    }
}
