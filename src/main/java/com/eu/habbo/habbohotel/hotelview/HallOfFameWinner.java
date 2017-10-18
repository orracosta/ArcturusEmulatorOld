package com.eu.habbo.habbohotel.hotelview;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallOfFameWinner implements Comparable<HallOfFameWinner>
{
    /**
     * Habbo ID
     */
    private int id;

    /**
     * Name
     */
    private String username;

    /**
     * Look
     */
    private String look;

    /**
     * Score
     */
    private int points;

    public HallOfFameWinner(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.username = set.getString("username");
        this.look = set.getString("look");
        this.points = set.getInt("hof_points");
    }

    /**
     * Habbo ID
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Name
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * Look
     */
    public String getLook()
    {
        return this.look;
    }

    /**
     * Score
     */
    public int getPoints()
    {
        return this.points;
    }

    @Override
    public int compareTo(HallOfFameWinner o)
    {
        return o.getPoints() - this.points;
    }
}
