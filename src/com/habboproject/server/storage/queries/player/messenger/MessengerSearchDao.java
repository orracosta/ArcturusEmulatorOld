package com.habboproject.server.storage.queries.player.messenger;

import com.habboproject.server.game.players.components.types.messenger.MessengerSearchResult;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MessengerSearchDao {
    public static List<MessengerSearchResult> performSearch(String query) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<MessengerSearchResult> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            query = SqlHelper.escapeWildcards(query);

            preparedStatement = SqlHelper.prepare("SELECT * FROM players WHERE username LIKE ? LIMIT 50;", sqlConnection);
            preparedStatement.setString(1, query + "%");

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.add(new MessengerSearchResult(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("figure"), resultSet.getString("motto"), new Date(resultSet.getInt("last_online") * 1000L).toString()));
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
