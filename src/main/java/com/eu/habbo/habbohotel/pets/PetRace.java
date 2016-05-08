package com.eu.habbo.habbohotel.pets;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 29-11-2014 15:34.
 */
public class PetRace
{
    public final int race;
    public final int colorOne;
    public final int colorTwo;
    public final boolean hasColorOne;
    public final boolean hasColorTwo;

    public PetRace(ResultSet set) throws SQLException
    {
        this.race = set.getInt("race");
        this.colorOne = set.getInt("color_one");
        this.colorTwo = set.getInt("color_two");
        this.hasColorOne = set.getString("has_color_one").equals("1");
        this.hasColorTwo = set.getString("has_color_two").equals("1");
    }
}
