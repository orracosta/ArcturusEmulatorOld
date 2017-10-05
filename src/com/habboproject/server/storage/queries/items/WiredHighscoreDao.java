package com.habboproject.server.storage.queries.items;

import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by brend on 03/02/2017.
 */
public class WiredHighscoreDao {
    public static String getData(long id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("SELECT * FROM items_wired_highscore WHERE item_id = ? LIMIT 1;", sqlConnection);
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String string = resultSet.getString("item_data");
                return string;
            }
        }
        catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        }
        finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return "";
    }

    public static void save(String data, long id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            try {
                sqlConnection = SqlHelper.getConnection();
                preparedStatement = SqlHelper.prepare("INSERT INTO items_wired_highscore VALUES (?, ?);", sqlConnection);
                preparedStatement.setLong(1, id);
                preparedStatement.setString(2, data);
                preparedStatement.execute();
            }
            catch (SQLException e) {
                SqlHelper.handleSqlException(e);
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        }
        finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void update(String data, long id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            try {
                sqlConnection = SqlHelper.getConnection();
                preparedStatement = SqlHelper.prepare("UPDATE items_wired_highscore SET item_data = ? WHERE item_id = ? LIMIT 1;", sqlConnection);
                preparedStatement.setString(1, data);
                preparedStatement.setLong(2, id);
                preparedStatement.execute();
            }
            catch (SQLException e) {
                SqlHelper.handleSqlException(e);
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        }
        finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
