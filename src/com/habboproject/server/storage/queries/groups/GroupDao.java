package com.habboproject.server.storage.queries.groups;

import com.google.common.collect.Lists;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class GroupDao {
    public static GroupData getDataById(int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM groups WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new GroupData(resultSet);
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }

    public static List<Integer> loadForums() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Integer> forums = Lists.newArrayList();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT id FROM groups WHERE has_forum = '1'", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (!forums.contains(resultSet.getInt("id"))) {
                    forums.add(resultSet.getInt("id"));
                }
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return forums;
    }

    public static int create(GroupData groupData) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into groups (`name`, `description`, `badge`, `owner_id`, `room_id`, `created`, `type`, `colour1`, `colour2`, `members_deco`, `has_forum`) " +
                    "VALUES(? ,?, ?, ?, ?, ?, ?, ?, ?, ?, '0');", sqlConnection, true);
            preparedStatement.setString(1, groupData.getTitle());
            preparedStatement.setString(2, groupData.getDescription());
            preparedStatement.setString(3, groupData.getBadge());
            preparedStatement.setInt(4, groupData.getOwnerId());
            preparedStatement.setInt(5, groupData.getRoomId());
            preparedStatement.setInt(6, groupData.getCreatedTimestamp());
            preparedStatement.setString(7, groupData.getType().toString().toLowerCase());
            preparedStatement.setInt(8, groupData.getColourA());
            preparedStatement.setInt(9, groupData.getColourB());
            preparedStatement.setString(10, groupData.canMembersDecorate() ? "1" : "0");

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

    public static int getIdByRoomId(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `id` FROM `groups` WHERE `room_id` = ?", sqlConnection, true);
            preparedStatement.setInt(1, roomId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getInt("id");
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

    public static void save(GroupData groupData) {
        if (groupData.getId() == -1) {
            Comet.getServer().getLogger().warn("Tried to update group data which doesn't exist/doesn't have a valid ID (-1)! Title: " + groupData.getTitle());
            return;
        }

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE groups SET name = ?, description = ?, badge = ?, owner_id = ?, room_id = ?, type = ?, colour1 = ?, colour2 = ?, members_deco = ?, has_forum = ? WHERE id = ?", sqlConnection, true);
            preparedStatement.setString(1, groupData.getTitle());
            preparedStatement.setString(2, groupData.getDescription());
            preparedStatement.setString(3, groupData.getBadge());
            preparedStatement.setInt(4, groupData.getOwnerId());
            preparedStatement.setInt(5, groupData.getRoomId());
            preparedStatement.setString(6, groupData.getType().toString().toLowerCase());
            preparedStatement.setInt(7, groupData.getColourA());
            preparedStatement.setInt(8, groupData.getColourB());
            preparedStatement.setString(9, groupData.canMembersDecorate() ? "1" : "0");
            preparedStatement.setString(10, groupData.hasForum() ? "1" : "0");

            preparedStatement.setInt(11, groupData.getId());

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void deleteGroup(int groupId) {
        Connection sqlConnection = null;
        try {
            sqlConnection = SqlHelper.getConnection();

            deleteGroupData(sqlConnection, groupId);
            deleteAllMemberships(sqlConnection, groupId);
            deleteAllRequests(sqlConnection, groupId);
            clearGroupFavourites(sqlConnection, groupId);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    private static void deleteGroupData(Connection sqlConnection, int groupId) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = sqlConnection.prepareStatement("DELETE FROM groups WHERE id = ?");
            preparedStatement.setInt(1, groupId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
        }
    }

    private static void deleteAllMemberships(Connection sqlConnection, int groupId) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = sqlConnection.prepareStatement("DELETE FROM group_memberships WHERE group_id = ?");
            preparedStatement.setInt(1, groupId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
        }
    }

    private static void deleteAllRequests(Connection sqlConnection, int groupId) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = sqlConnection.prepareStatement("DELETE FROM group_requests WHERE group_id = ?");
            preparedStatement.setInt(1, groupId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
        }
    }

    private static void clearGroupFavourites(Connection sqlConnection, int groupId) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = sqlConnection.prepareStatement("UPDATE players SET favourite_group = 0 WHERE favourite_group = ?");
            preparedStatement.setInt(1, groupId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
        }
    }

    public static List<Integer> getIdsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Integer> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `group_id` FROM group_memberships WHERE player_id = ?", sqlConnection, true);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.add(resultSet.getInt("group_id"));
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
