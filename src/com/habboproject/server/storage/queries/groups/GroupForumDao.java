package com.habboproject.server.storage.queries.groups;

import com.habboproject.server.game.groups.types.components.forum.settings.ForumPermission;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumSettings;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupForumDao {
    public static ForumSettings createSettings(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO group_forum_settings (`group_id`) VALUES(?);", sqlConnection, true);

            preparedStatement.setInt(1, groupId);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return new ForumSettings(groupId, ForumPermission.EVERYBODY, ForumPermission.EVERYBODY, ForumPermission.EVERYBODY,
                ForumPermission.ADMINISTRATORS);
    }

    public static ForumSettings getSettings(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM group_forum_settings WHERE group_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new ForumSettings(
                        groupId,
                        ForumPermission.valueOf(resultSet.getString("read_permission")),
                        ForumPermission.valueOf(resultSet.getString("post_permission")),
                        ForumPermission.valueOf(resultSet.getString("thread_permission")),
                        ForumPermission.valueOf(resultSet.getString("moderate_permission"))
                );
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }

    public static void saveSettings(ForumSettings forumSettings) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE group_forum_settings SET read_permission = ?, " +
                    "post_permission = ?, thread_permission = ?, moderate_permission = ? WHERE group_id = ?", sqlConnection);

            preparedStatement.setString(1, forumSettings.getReadPermission().toString());
            preparedStatement.setString(2, forumSettings.getPostPermission().toString());
            preparedStatement.setString(3, forumSettings.getStartThreadsPermission().toString());
            preparedStatement.setString(4, forumSettings.getModeratePermission().toString());

            preparedStatement.setInt(5, forumSettings.getGroupId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
