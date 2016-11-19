package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.navigation.SearchMode;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("NullableProblems")
public class RoomCategory implements Comparable<RoomCategory> {

    private int id;
    private int minRank;
    private String caption;
    private boolean canTrade;
    private int maxUserCount;
    private boolean official;
    private SearchMode displayMode;

    public RoomCategory(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.minRank = set.getInt("min_rank");
        this.caption = set.getString("caption");
        this.canTrade = set.getBoolean("can_trade");
        this.maxUserCount = set.getInt("max_user_count");
        this.official = set.getString("public").equals("1");
        this.displayMode = SearchMode.fromType(set.getInt("list_type"));
    }

    public int getId() {
        return this.id;
    }

    public int getMinRank() {
        return this.minRank;
    }

    public String getCaption() {
        return this.caption;
    }

    public boolean isCanTrade() {
        return this.canTrade;
    }

    public int getMaxUserCount()
    {
        return this.maxUserCount;
    }

    public boolean isPublic()
    {
        return this.official;
    }

    public SearchMode getDisplayMode()
    {
        return this.displayMode;
    }

    @Override
    public int compareTo(RoomCategory o) {
        return o.getId() - this.getId();
    }
}
