package com.habboproject.server.storage.queries.rooms;

import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class RightsDao {
    public static List<Integer> getRightsByRoomId(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Integer> data = new CopyOnWriteArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT player_id FROM room_rights WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);

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

    public static void delete(int playerId, int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE from room_rights WHERE room_id = ? AND player_id = ? ", sqlConnection);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, playerId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void add(int playerId, int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into room_rights (`room_id`, `player_id`) VALUES(?, ?);", sqlConnection);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, playerId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
