package com.eu.habbo.habbohotel.modtool;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 4-11-2014 16:15.
 */
public class ModToolPreset
{
    public final int id;
    public final String name;
    public final String message;
    public final String reminder;
    public final int banLength;
    public final int muteLength;

    public ModToolPreset(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.message = set.getString("message");
        this.reminder = set.getString("reminder");
        this.banLength = set.getInt("ban_for");
        this.muteLength = set.getInt("mute_for");
    }
}
