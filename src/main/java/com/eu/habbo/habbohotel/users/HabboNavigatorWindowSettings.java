package com.eu.habbo.habbohotel.users;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 19-11-2015 20:40.
 */
public class HabboNavigatorWindowSettings
{
    public int x = 100;
    public int y = 100;
    public int width = 425;
    public int height = 535;
    public boolean openSearches = false;
    public int unknown = 0;

    public HabboNavigatorWindowSettings(){}

    public HabboNavigatorWindowSettings(ResultSet set) throws SQLException
    {
        this.x = set.getInt("x");
        this.y = set.getInt("y");
        this.width = set.getInt("width");
        this.height = set.getInt("height");
        this.openSearches = set.getString("open_searches").equals("1");
        this.unknown = 0;
    }
}
