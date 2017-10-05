package com.habboproject.server.storage.queries.bots;

import com.habboproject.server.game.bots.BotData;
import com.habboproject.server.game.rooms.objects.entities.types.data.PlayerBotData;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.utilities.JsonFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class RoomBotDao {
    public static List<BotData> getBotsByRoomId(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<BotData> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM bots WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                PlayerBotData botData = new PlayerBotData(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("motto"), resultSet.getString("figure"), resultSet.getString("gender"), resultSet.getString("owner"), resultSet.getInt("owner_id"), resultSet.getString("messages"), resultSet.getString("automatic_chat").equals("1"), resultSet.getInt("chat_delay"), resultSet.getString("type"), resultSet.getString("mode"), resultSet.getString("data"));
                botData.setPosition(new Position(resultSet.getInt("x"), resultSet.getInt("y")));

                data.add(botData);
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

    public static void setRoomId(int roomId, int botId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE bots SET room_id = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, botId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveData(BotData data) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE bots SET figure = ?, gender = ?, motto = ?, name = ?, messages = ?, automatic_chat = ?, chat_delay = ?, mode = ?, data = ? WHERE id = ?", sqlConnection);

            preparedStatement.setString(1, data.getFigure());
            preparedStatement.setString(2, data.getGender());
            preparedStatement.setString(3, data.getMotto());
            preparedStatement.setString(4, data.getUsername());
            preparedStatement.setString(5, JsonFactory.getInstance().toJson(data.getMessages()));
            preparedStatement.setString(6, data.isAutomaticChat() ? "1" : "0");
            preparedStatement.setInt(7, data.getChatDelay());
            preparedStatement.setString(8, data.getMode());
            preparedStatement.setString(9, data.getData());

            preparedStatement.setInt(10, data.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void savePosition(int x, int y, double height, int botId, int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE bots SET x = ?, y = ?, z = ?, room_id = ? WHERE id = ?", sqlConnection);

            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setDouble(3, height);
            preparedStatement.setInt(4, roomId);
            preparedStatement.setInt(5, botId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
