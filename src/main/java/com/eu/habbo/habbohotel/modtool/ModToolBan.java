package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModToolBan implements Runnable
{
    public int userId;
    public String ip;
    public int staffId;
    public int expireDate;
    public String reason;
    public String type;

    private boolean needsInsert;

    public ModToolBan(ResultSet set) throws SQLException
    {
        this.userId = set.getInt("user_id");
        this.ip = set.getString("ip");
        this.staffId = set.getInt("user_staff_id");
        this.expireDate = set.getInt("ban_expire");
        this.reason = set.getString("ban_reason");
        this.type = set.getString("type");
        this.needsInsert = false;
    }

    public ModToolBan(int userId, String ip, int staffId, int expireDate, String reason, String type)
    {
        this.userId = userId;
        this.staffId = staffId;
        this.expireDate = expireDate;
        this.reason = reason;
        this.needsInsert = true;
    }

    @Override
    public void run()
    {
        if(this.needsInsert)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO bans (user_id, ip, user_staff_id, ban_expire, ban_reason, type) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setInt(1, this.userId);
                statement.setString(2, this.ip);
                statement.setInt(3, this.staffId);
                statement.setInt(4, this.expireDate);
                statement.setString(5, this.reason);
                statement.setString(6, this.type);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }
}
