package com.habboproject.server.game.permissions.types;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Perk {
    private int id;
    private String title;
    private String data;
    private int rank;
    private boolean overrideRank;
    private boolean overrideDefault;

    public Perk(ResultSet result) throws SQLException {
        this.id = result.getInt("id");
        this.title = result.getString("title");
        this.data = result.getString("data");
        this.rank = result.getInt("min_rank");
        this.overrideRank = result.getString("override_rank").equals("1");
        this.overrideDefault = result.getString("override_default").equals("1");
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getData() {
        return this.data;
    }

    public int getRank() {
        return this.rank;
    }

    public boolean doesOverride() {
        return this.overrideRank;
    }

    public boolean getDefault() {
        return this.overrideDefault;
    }
}
