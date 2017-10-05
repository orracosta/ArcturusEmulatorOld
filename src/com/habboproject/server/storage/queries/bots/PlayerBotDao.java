package com.habboproject.server.storage.queries.bots;

import com.habboproject.server.api.game.players.data.components.bots.PlayerBot;
import com.habboproject.server.game.players.components.types.inventory.InventoryBot;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerBotDao {
    public static Map<Integer, PlayerBot> getBotsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, PlayerBot> data = new ConcurrentHashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM bots WHERE owner_id = ? AND room_id = 0", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getInt("id"), new InventoryBot(resultSet));
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

    public static int createBot(int playerId, String name, String figure, String gender, String motto, String type) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into bots (`owner_id`, `room_id`, `name`, `figure`, `gender`, `motto`, `x`, `y`, `z`, `messages`, `automatic_chat`, `chat_delay`, `type`) VALUES(" +
                    "?, 0, ?, ?, ?, ?, 0, 0, 0, '[]', '1', '14', ?);", sqlConnection, true);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, figure);
            preparedStatement.setString(4, gender);
            preparedStatement.setString(5, motto);
            preparedStatement.setString(6, type);

            preparedStatement.execute();

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

    public static void deleteBots(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM bots WHERE owner_id = ? AND room_id = 0;", sqlConnection);
            preparedStatement.setInt(1, playerId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
