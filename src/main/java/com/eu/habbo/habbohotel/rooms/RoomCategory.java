package com.eu.habbo.habbohotel.rooms;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 31-8-2014 14:56.
 */
@SuppressWarnings("NullableProblems")
public class RoomCategory implements Comparable<RoomCategory> {

    private int id;
    private int minRank;
    private String caption;
    private boolean canTrade;

    public RoomCategory(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.minRank = set.getInt("min_rank");
        this.caption = set.getString("caption");
        this.canTrade = set.getBoolean("can_trade");
    }

    public int getId() {
        return id;
    }

    public int getMinRank() {
        return minRank;
    }

    public String getCaption() {
        return caption;
    }

    public boolean isCanTrade() {
        return canTrade;
    }

    @Override
    public int compareTo(RoomCategory o) {
        return o.getId() - this.getId();
    }
}
