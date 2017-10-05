package com.habboproject.server.storage.queries.moderation;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.moderation.types.Ban;
import com.habboproject.server.game.moderation.types.BanType;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class BanDao {
    public static Map<String, Ban> getActiveBans() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, Ban> data = new ConcurrentHashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM bans WHERE expire = 0 OR expire > " + Comet.getTime(), sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getString("data"), new Ban(resultSet));
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

    public static int createBan(BanType type, long length, long expire, String data, int addedBy, String reason) {

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into bans (`type`, `expire`, `data`, `reason`, `added_by`) VALUES(?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setString(1, type.toString().toLowerCase());
            preparedStatement.setLong(2, length == 0 ? 0 : expire);
            preparedStatement.setString(3, data);
            preparedStatement.setString(4, reason);
            preparedStatement.setInt(5, addedBy);

            SqlHelper.executeStatementSilently(preparedStatement, false);
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
}
