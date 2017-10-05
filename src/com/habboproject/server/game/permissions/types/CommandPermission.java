package com.habboproject.server.game.permissions.types;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CommandPermission {
    private String commandId;
    private int minimumRank;
    private boolean vipOnly;

    public CommandPermission(ResultSet data) throws SQLException {
        this.commandId = data.getString("command_id");
        this.minimumRank = data.getInt("minimum_rank");
        this.vipOnly = data.getString("vip_only").equals("1");
    }

    public String getCommandId() {
        return commandId;
    }

    public int getMinimumRank() {
        return minimumRank;
    }

    public boolean isVipOnly() {
        return vipOnly;
    }
}
