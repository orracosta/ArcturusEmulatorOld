package com.habboproject.server.logging.database;

import com.habboproject.server.logging.database.queries.LogQueries;
import com.habboproject.server.storage.SqlHelper;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LogDatabaseHelper {
    private static Logger log = Logger.getLogger(LogDatabaseHelper.class.getName());

    public static void init() {
        LogQueries.updateRoomEntries();
    }

    public static Connection getConnection() throws SQLException {
        return SqlHelper.getConnection();
    }

    public static void closeSilently(Connection connection) {
        try {
            if (connection == null) {
                return;
            }
            connection.close();
        } catch (SQLException e) {
            handleSqlException(e);
        }
    }

    public static void closeSilently(ResultSet resultSet) {
        try {
            if (resultSet == null) {
                return;
            }
            resultSet.close();
        } catch (SQLException e) {
            handleSqlException(e);
        }
    }

    public static void closeSilently(PreparedStatement statement) {
        try {
            if (statement == null) {
                return;
            }
            statement.close();
        } catch (SQLException e) {
            handleSqlException(e);
        }
    }

    public static void executeStatementSilently(PreparedStatement statement, boolean autoClose) {
        try {
            if (statement == null) {
                return;
            }
            statement.execute();

            if (autoClose) {
                statement.close();
            }
        } catch (SQLException e) {
            handleSqlException(e);
        }
    }

    public static PreparedStatement prepare(String query, Connection con) throws SQLException {
        return prepare(query, con, false);
    }

    public static PreparedStatement prepare(String query, Connection con, boolean returnKeys) throws SQLException {
        return returnKeys ? con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS) : con.prepareStatement(query);
    }

    public static void handleSqlException(SQLException e) {
        log.error("Error while executing query", e);
    }

}
