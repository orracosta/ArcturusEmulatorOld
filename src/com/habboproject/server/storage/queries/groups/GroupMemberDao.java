package com.habboproject.server.storage.queries.groups;

import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class GroupMemberDao {
    public static List<GroupMember> getAllByGroupId(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<GroupMember> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM group_memberships WHERE group_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.add(new GroupMember(resultSet));
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

    public static void save(GroupMember groupMember) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE group_memberships SET access_level = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, groupMember.getAccessLevel().toString().toLowerCase());
            preparedStatement.setInt(2, groupMember.getMembershipId());

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static int create(GroupMember groupMember) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into group_memberships (`group_id`, `player_id`, `access_level`, `date_joined`) VALUES(?, ?, ?, ?);", sqlConnection, true);
            preparedStatement.setInt(1, groupMember.getGroupId());
            preparedStatement.setInt(2, groupMember.getPlayerId());
            preparedStatement.setString(3, groupMember.getAccessLevel().toString().toLowerCase());
            preparedStatement.setInt(4, groupMember.getDateJoined());

            SqlHelper.executeStatementSilently(preparedStatement, false);

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return 0;
    }

    public static void delete(int groupMembershipId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM group_memberships WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, groupMembershipId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void createRequest(int groupId, int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into group_requests(`group_id`, `player_id`) VALUES(?, ?);", sqlConnection);

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, playerId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void deleteRequest(int groupId, int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM group_requests WHERE group_id = ? AND player_id = ?", sqlConnection);

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, playerId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void clearRequests(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM group_requests WHERE group_id = ?", sqlConnection);

            preparedStatement.setInt(1, groupId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static List<Integer> getAllRequestsByGroupId(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Integer> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM group_requests WHERE group_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.add(resultSet.getInt("player_id"));
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
}

