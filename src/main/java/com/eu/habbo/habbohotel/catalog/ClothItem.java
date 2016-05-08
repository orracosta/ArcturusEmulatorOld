package com.eu.habbo.habbohotel.catalog;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 15-2-2015 11:27.
 */
class ClothItem
{
    /**
     * Identifier.
     */
    private int id;

    /**
     * Name
     */
    private String name;

    /**
     * Set
     */
    private String setId;

    public ClothItem(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.setId = set.getString("setid");
    }
}
