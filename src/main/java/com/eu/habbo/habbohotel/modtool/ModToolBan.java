package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class ModToolBan implements Runnable
{
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public int userId;
    public String ip;
    public String machineId;
    public int staffId;
    public int expireDate;
    public int timestamp;
    public String reason;
    public ModToolBanType type;
    public int cfhTopic;

    private boolean needsInsert;

    public ModToolBan(ResultSet set) throws SQLException
    {
        this.userId      = set.getInt("user_id");
        this.ip          = set.getString("ip");
        this.machineId   = set.getString("machine_id");
        this.staffId     = set.getInt("user_staff_id");
        this.timestamp   = set.getInt("timestamp");
        this.expireDate  = set.getInt("ban_expire");
        this.reason      = set.getString("ban_reason");
        this.type        = ModToolBanType.fromString(set.getString("type"));
        this.cfhTopic    = set.getInt("cfh_topic");
        this.needsInsert = false;
    }

    public ModToolBan(int userId, String ip, String machineId, int staffId, int expireDate, String reason, ModToolBanType type, int cfhTopic)
    {
        this.userId      = userId;
        this.staffId     = staffId;
        this.timestamp   = Emulator.getIntUnixTimestamp();
        this.expireDate  = expireDate;
        this.reason      = reason;
        this.ip          = ip;
        this.machineId   = machineId;
        this.type        = type;
        this.cfhTopic    = cfhTopic;
        this.needsInsert = true;
    }

    @Override
    public void run()
    {
        if(this.needsInsert)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO bans (user_id, ip, machine_id, user_staff_id, timestamp, ban_expire, ban_reason, type, cfh_topic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"))
            {
                statement.setInt(1, this.userId);
                statement.setString(2, this.ip);
                statement.setString(3, this.machineId);
                statement.setInt(4, this.staffId);
                statement.setInt(5, Emulator.getIntUnixTimestamp());
                statement.setInt(6, this.expireDate);
                statement.setString(7, this.reason);
                statement.setString(8, this.type.getType());
                statement.setInt(9, this.cfhTopic);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public String listInfo()
    {
        return new StringBuilder()
                .append("Banned User Id: ") .append(this.userId)        .append("\r")
                .append("Type: ")           .append(this.type.getType()).append("\r")
                .append("Reason: ")         .append("<i>").append(this.reason).append("</i>").append("\r")
                .append("Moderator Id: ")   .append(this.staffId)       .append("\r")
                .append("Date: ")           .append(dateFormat.format(this.timestamp * 1000l))     .append("\r")
                .append("Expire Date: ")    .append(dateFormat.format(this.expireDate * 1000l))    .append("\r")
                .append("IP: ")             .append(this.ip)            .append("\r")
                .append("MachineID: ")      .append(this.machineId)     .append("\r")
                .append("Topic: ")          .append(this.cfhTopic).toString();
    }
}
