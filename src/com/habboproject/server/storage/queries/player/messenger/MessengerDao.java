package com.habboproject.server.storage.queries.player.messenger;

import com.habboproject.server.game.players.components.types.messenger.MessengerFriend;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessengerDao {
    public static Map<Integer, MessengerFriend> getFriendsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, MessengerFriend> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT m.user_two_id, p.username, p.figure, p.motto FROM messenger_friendships m LEFT JOIN players p ON p.id = m.user_two_id WHERE user_one_id = ?;", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getInt("user_two_id"), new MessengerFriend(resultSet));
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

    public static List<Integer> getRequestsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Integer> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM messenger_requests WHERE to_id = ?", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.add(resultSet.getInt("from_id"));
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

    public static void deleteFriendship(int userId, int userTwoId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE from messenger_friendships WHERE user_one_id = ? AND user_two_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userTwoId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void deleteRequestDataByRecieverId(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM messenger_requests WHERE to_id = ?", sqlConnection);

            preparedStatement.setInt(1, userId);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void deleteRequestData(int userId, int userTwoId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM messenger_requests WHERE to_id = ? AND from_id = ?", sqlConnection);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userTwoId);
            preparedStatement.addBatch();

            preparedStatement.setInt(1, userTwoId);
            preparedStatement.setInt(2, userId);
            preparedStatement.addBatch();

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void createFriendship(int userId, int userTwoId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into messenger_friendships VALUES(?, ?)", sqlConnection);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userTwoId);
            preparedStatement.addBatch();

            preparedStatement.setInt(1, userTwoId);
            preparedStatement.setInt(2, userId);
            preparedStatement.addBatch();

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void createRequest(int userId, int userTwoId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into messenger_requests (`from_id`, `to_id`) VALUES(?, ?)", sqlConnection);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userTwoId);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static int getFriendCount(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT COUNT(1) FROM messenger_friendships WHERE user_one_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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
}
