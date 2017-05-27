package com.eu.habbo.habbohotel.modtool;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CfhTopic
{
    public final int id;
    public final String name;
    public final CfhActionType action;
    public final String reply;

    public CfhTopic(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.name = set.getString("name_internal");
        this.action = CfhActionType.get(set.getString("action"));
        this.reply = set.getString("auto_reply");
    }
}