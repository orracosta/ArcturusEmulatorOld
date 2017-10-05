package com.habboproject.server.game.groups.items.types;

import java.sql.ResultSet;
import java.sql.SQLException;


public class GroupSymbol {
    private int id;
    private String valueA;
    private String valueB;

    public GroupSymbol(ResultSet data) throws SQLException {
        this.id = data.getInt("id");
        this.valueA = data.getString("firstvalue");
        this.valueB = data.getString("secondvalue");
    }

    public int getId() {
        return id;
    }

    public String getValueA() {
        return valueA;
    }

    public String getValueB() {
        return valueB;
    }
}
