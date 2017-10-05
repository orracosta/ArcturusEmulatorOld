package com.habboproject.server.storage.queries.quests;

import com.habboproject.server.game.quests.types.Quest;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QuestsDao {
    public static Map<String, Quest> getAllQuests() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, Quest> quests = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM quests WHERE enabled = '1'", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                quests.put(resultSet.getString("name"), new Quest(resultSet));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return quests;
    }
}
