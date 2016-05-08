package com.eu.habbo.habbohotel.catalog;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Voucher
{
    /**
     * Voucher ID
     */
    private final int id;

    /**
     * Code
     */
    public final String code;

    /**
     * Reward credits.
     */
    public final int credits;

    /**
     * Reward points.
     */
    public final int points;

    /**
     * Points type
     */
    public final int pointsType;

    /**
     * Constructs a new Voucher.
     * @param set The ResultSet to read the data from.
     * @throws SQLException
     */
    public Voucher(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.code = set.getString("code");
        this.credits = set.getInt("credits");
        this.points = set.getInt("points");
        this.pointsType = set.getInt("points_type");
    }
}
