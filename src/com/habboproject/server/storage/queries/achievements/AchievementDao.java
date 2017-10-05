package com.habboproject.server.storage.queries.achievements;

import com.habboproject.server.game.achievements.AchievementGroup;
import com.habboproject.server.game.achievements.types.Achievement;
import com.habboproject.server.game.achievements.types.AchievementCategory;
import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AchievementDao {

    public static int getAchievements(Map<AchievementType, AchievementGroup> achievementGroups) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM achievements WHERE enabled = '1' ORDER by group_name ASC", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                count++;

                final AchievementType groupName = AchievementType.getTypeByName(resultSet.getString("group_name"));

                if (groupName == null) continue;

                if (!achievementGroups.containsKey(groupName)) {
                    achievementGroups.put(groupName, new AchievementGroup(resultSet.getInt("id"), new HashMap<>(), resultSet.getString("group_name"), AchievementCategory.valueOf(resultSet.getString("category").toUpperCase())));
                }

                if (!achievementGroups.get(groupName).getAchievements().containsKey(resultSet.getInt("level"))) {
                    achievementGroups.get(groupName).getAchievements().put(resultSet.getInt("level"), create(resultSet));
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return count;
    }

    private static Achievement create(ResultSet resultSet) throws SQLException {
        return new Achievement(resultSet.getInt("level"), resultSet.getInt("reward_activity_points"), resultSet.getInt("reward_achievement_points"), resultSet.getInt("progress_requirement"));
    }

}
