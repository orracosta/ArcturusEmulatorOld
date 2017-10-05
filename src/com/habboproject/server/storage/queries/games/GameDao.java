package com.habboproject.server.storage.queries.games;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.utilities.RandomInteger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by brend on 01/03/2017.
 */
public class GameDao {
    public static String generatePlayerToken(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        String token = "" + playerId;
        token += RandomInteger.getRandom(1, 100);
        token += Comet.getServer().getConfig().get("comet.fastfood.server");

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE players SET fastfood_token = ? WHERE id = ? LIMIT 1",
                    sqlConnection);

            preparedStatement.setString(1, token);
            preparedStatement.setInt(2, playerId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
            SqlHelper.closeSilently(preparedStatement);
        }

        return token;
    }
}
