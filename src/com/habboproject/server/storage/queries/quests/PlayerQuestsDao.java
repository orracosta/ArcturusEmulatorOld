package com.habboproject.server.storage.queries.quests;

import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PlayerQuestsDao {
    public static Map<Integer, Integer> getQuestProgression(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, Integer> questProgression = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `quest_id`, `progress`  FROM player_quest_progression WHERE player_id = ?", sqlConnection);

            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                questProgression.put(resultSet.getInt("quest_id"), resultSet.getInt("progress"));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return questProgression;
    }

    public static void saveProgression(boolean isNew, int playerId, int questId, int progression) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            if (isNew) {
                preparedStatement = SqlHelper.prepare("INSERT into player_quest_progression (progress, player_id, quest_id) VALUES(?, ?, ?);", sqlConnection);
            } else {
                preparedStatement = SqlHelper.prepare("UPDATE player_quest_progression SET progress = ? WHERE player_id = ? AND quest_id = ?;", sqlConnection);
            }

            preparedStatement.setInt(1, progression);
            preparedStatement.setInt(2, playerId);
            preparedStatement.setInt(3, questId);

            if (isNew) {
                preparedStatement.execute();
            } else {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void cancelQuest(int questId, int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM player_quest_progression WHERE player_id = ? AND quest_id = ?;", sqlConnection);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, questId);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
