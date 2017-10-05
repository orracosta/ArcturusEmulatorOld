package com.habboproject.server.game.moderation.types.actions;

import com.habboproject.server.storage.queries.moderation.PresetDao;
import com.google.common.collect.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class ActionCategory {
    private int categoryId;
    private String categoryName;

    private List<ActionPreset> presets = Lists.newArrayList();

    public ActionCategory(ResultSet resultSet) throws SQLException {
        this.categoryId = resultSet.getInt("id");
        this.categoryName = resultSet.getString("name");

        PresetDao.getActionPresetsForCategory(this.categoryId, presets);
    }

    public void dispose() {
        this.presets.clear();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<ActionPreset> getPresets() {
        return presets;
    }
}
