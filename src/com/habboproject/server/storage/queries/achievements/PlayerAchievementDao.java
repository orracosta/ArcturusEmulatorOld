package com.habboproject.server.storage.queries.achievements;

import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.players.components.types.achievements.AchievementProgress;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PlayerAchievementDao {
    public static Map<AchievementType, AchievementProgress> getAchievementProgress(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<AchievementType, AchievementProgress> achievements = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `group`, `level`, `progress` FROM `player_achievements` WHERE `player_id` = ?", sqlConnection);

            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                achievements.put(AchievementType.getTypeByName(resultSet.getString("group")), new AchievementProgress(resultSet.getInt("level"), resultSet.getInt("progress")));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return achievements;
    }

    public static void saveProgress(int playerId, AchievementType type, AchievementProgress progress) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into player_achievements (`player_id`, `group`, `level`, `progress`) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE level = ?, progress = ?;", sqlConnection);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setString(2, type.getGroupName());
            preparedStatement.setInt(3, progress.getLevel());
            preparedStatement.setInt(4, progress.getProgress());
            preparedStatement.setInt(5, progress.getLevel());
            preparedStatement.setInt(6, progress.getProgress());

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void updateBadge(String oldBadge, String newBadge, int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_badges SET badge_code = ? WHERE player_id = ? AND badge_code = ?", sqlConnection);

            preparedStatement.setString(1, newBadge);
            preparedStatement.setInt(2, playerId);
            preparedStatement.setString(3, oldBadge);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
