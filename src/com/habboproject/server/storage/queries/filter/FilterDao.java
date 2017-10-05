package com.habboproject.server.storage.queries.filter;

import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class FilterDao {
    public static Map<String, String> loadWordfilter() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, String> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `word`, `replacement` FROM `wordfilter`", sqlConnection);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getString("word").toLowerCase(), resultSet.getString("replacement"));
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

    public static void save(Map<String, String> wordfilter) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("REPLACE INTO wordfilter (`word`, `replacement`) VALUES(?, ?)", sqlConnection);

            for (Map.Entry<String, String> word : wordfilter.entrySet()) {
                preparedStatement.setString(1, word.getKey());
                preparedStatement.setString(2, word.getValue());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
