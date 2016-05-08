package com.eu.habbo.habbohotel.catalog;

import java.sql.ResultSet;
import java.sql.SQLException;

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
