package com.habboproject.server.storage.queries.items;

import com.habboproject.server.game.items.rares.LimitedEditionItemData;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LimitedEditionDao {
    public static void save(LimitedEditionItemData item) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO items_limited_edition (`item_id`, `limited_id`, `limited_total`) VALUES(?, ?, ?);", sqlConnection);
            preparedStatement.setLong(1, item.getItemId());
            preparedStatement.setInt(2, item.getLimitedRare());
            preparedStatement.setInt(3, item.getLimitedRareTotal());

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static LimitedEditionItemData get(long itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `limited_id`, `limited_total` FROM items_limited_edition WHERE item_id = ? LIMIT 1;", sqlConnection);
            preparedStatement.setLong(1, itemId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new LimitedEditionItemData(itemId, resultSet.getInt("limited_id"), resultSet.getInt("limited_total"));
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
}
