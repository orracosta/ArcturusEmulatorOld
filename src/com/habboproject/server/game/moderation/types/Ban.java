package com.habboproject.server.game.moderation.types;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Ban {
    private int id;
    private String data;
    private long expire;
    private BanType type;
    private String reason;

    public Ban(ResultSet data) throws SQLException {
        this.id = data.getInt("id");
        this.data = data.getString("data");
        this.expire = data.getInt("expire");
        this.type = BanType.getType(data.getString("type"));
        this.reason = data.getString("reason");
    }

    public Ban(int id, String data, long expire, BanType type, String reason) {
        this.id = id;
        this.data = data;
        this.expire = expire;
        this.type = type;
        this.reason = reason;
    }

    public int getId() {
        return this.id;
    }

    public String getData() {
        return this.data;
    }

    public long getExpire() {
        return this.expire;
    }

    public BanType getType() {
        return this.type;
    }

    public String getReason() {
        return this.reason;
    }
}
