package com.habboproject.server.game.moderation.types.actions;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ActionPreset {
    private int id;
    private int categoryId;
    private String name;
    private String message;
    private String description;

    private int banLength;
    private int avatarBanLength;
    private int muteLength;
    private int tradeLockLength;

    public ActionPreset(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.categoryId = resultSet.getInt("category_id");
        this.name = resultSet.getString("name");
        this.message = resultSet.getString("message");
        this.description = resultSet.getString("description");

        this.banLength = resultSet.getInt("ban_hours");
        this.avatarBanLength = resultSet.getInt("avatar_ban_hours");
        this.muteLength = resultSet.getInt("mute_hours");
        this.tradeLockLength = resultSet.getInt("trade_lock_hours");
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public int getBanLength() {
        return banLength;
    }

    public int getAvatarBanLength() {
        return avatarBanLength;
    }

    public int getMuteLength() {
        return muteLength;
    }

    public int getTradeLockLength() {
        return tradeLockLength;
    }
}
