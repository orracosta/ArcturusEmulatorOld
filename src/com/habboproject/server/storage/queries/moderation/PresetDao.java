package com.habboproject.server.storage.queries.moderation;

import com.habboproject.server.game.moderation.types.actions.ActionCategory;
import com.habboproject.server.game.moderation.types.actions.ActionPreset;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class PresetDao {
    public static void getPresets(List<String> userPresets, List<String> roomPresets) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM moderation_presets", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                switch (resultSet.getString("type")) {
                    case "user":
                        userPresets.add(resultSet.getString("message"));
                        break;

                    case "room":
                        roomPresets.add(resultSet.getString("message"));
                        break;
                }
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void getPresetActions(List<ActionCategory> actionCategories) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM moderation_action_categories", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                actionCategories.add(new ActionCategory(resultSet));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void getActionPresetsForCategory(int category, List<ActionPreset> presets) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM moderation_actions WHERE category_id = ?", sqlConnection);

            preparedStatement.setInt(1, category);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                presets.add(new ActionPreset(resultSet));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
