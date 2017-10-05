package com.habboproject.server.storage.queries.items;

import com.habboproject.server.game.rooms.objects.items.data.MoodlightData;
import com.habboproject.server.game.rooms.objects.items.data.MoodlightPresetData;
import com.habboproject.server.game.rooms.objects.items.types.wall.MoodlightWallItem;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.utilities.JsonFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MoodlightDao {
    public static MoodlightData getMoodlightData(long itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<MoodlightPresetData> presets = new ArrayList<>();
        MoodlightData data = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM items_moodlight WHERE item_id = ?", sqlConnection);
            preparedStatement.setLong(1, itemId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.isBeforeFirst()) {
                if (resultSet.next()) {
                    String preset1 = resultSet.getString("preset_1");
                    String preset2 = resultSet.getString("preset_2");
                    String preset3 = resultSet.getString("preset_3");

                    if (!preset1.equals("")) {
                        presets.add(JsonFactory.getInstance().fromJson(preset1, MoodlightPresetData.class));
                    }
                    if (!preset2.equals("")) {
                        presets.add(JsonFactory.getInstance().fromJson(preset2, MoodlightPresetData.class));
                    }
                    if (!preset3.equals("")) {
                        presets.add(JsonFactory.getInstance().fromJson(preset3, MoodlightPresetData.class));
                    }

                    data = new MoodlightData(resultSet.getString("enabled").equals("1"), resultSet.getInt("active_preset"), presets);
                }
            } else {
                presets.add(new MoodlightPresetData(true, "#000000", 255));
                presets.add(new MoodlightPresetData(true, "#000000", 255));
                presets.add(new MoodlightPresetData(true, "#000000", 255));

                preparedStatement = SqlHelper.prepare("INSERT INTO items_moodlight (item_id,enabled,active_preset,preset_1,preset_2,preset_3) VALUES (?,?,?,?,?,?);", sqlConnection);
                preparedStatement.setLong(1, itemId);
                preparedStatement.setString(2, "0");
                preparedStatement.setString(3, "1");
                preparedStatement.setString(4, JsonFactory.getInstance().toJson(presets.get(0)));
                preparedStatement.setString(5, JsonFactory.getInstance().toJson(presets.get(1)));
                preparedStatement.setString(6, JsonFactory.getInstance().toJson(presets.get(2)));

                preparedStatement.execute();

                data = new MoodlightData(false, 1, presets);
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return data;
    }

    public static void updateMoodlight(MoodlightWallItem item) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items_moodlight SET enabled = ?, active_preset = ?, preset_1 = ?, preset_2 = ?, preset_3 = ? WHERE item_id = ?", sqlConnection);
            preparedStatement.setString(1, item.getMoodlightData().isEnabled() ? "1" : "0");
            preparedStatement.setInt(2, item.getMoodlightData().getActivePreset());
            preparedStatement.setString(3, JsonFactory.getInstance().toJson(item.getMoodlightData().getPresets().get(0)));
            preparedStatement.setString(4, JsonFactory.getInstance().toJson(item.getMoodlightData().getPresets().get(1)));
            preparedStatement.setString(5, JsonFactory.getInstance().toJson(item.getMoodlightData().getPresets().get(2)));
            preparedStatement.setLong(6, item.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
