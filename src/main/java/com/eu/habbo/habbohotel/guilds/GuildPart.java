package com.eu.habbo.habbohotel.guilds;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 22-11-2014 16:42.
 */
public class GuildPart
{
    /**
     * Identifier.
     */
    public final int id;

    /**
     * Part A
     */
    public final String valueA;

    /**
     * Part B
     */
    public final String valueB;

    public GuildPart(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.valueA = set.getString("firstvalue");
        this.valueB = set.getString("secondvalue");
    }
}
