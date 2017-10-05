package com.habboproject.server.logging.database.queries;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.logging.AbstractLogEntry;
import com.habboproject.server.logging.database.LogDatabaseHelper;
import com.habboproject.server.logging.entries.RoomChatLogEntry;
import com.habboproject.server.logging.entries.RoomVisitLogEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LogQueries {
    public static void putEntry(AbstractLogEntry entry) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("INSERT into logs (`type`, `room_id`, `user_id`, `data`, `timestamp`) VALUES(?, ?, ?, ?, ?);", sqlConnection);

            preparedStatement.setString(1, entry.getType().toString());
            preparedStatement.setInt(2, entry.getRoomId());
            preparedStatement.setInt(3, entry.getPlayerId());
            preparedStatement.setString(4, entry.getString());
            preparedStatement.setInt(5, entry.getTimestamp());

            LogDatabaseHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
        }
    }

    public static void putEntryBatch(List<AbstractLogEntry> entries) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("INSERT into logs (`type`, `room_id`, `user_id`, `data`, `timestamp`) VALUES(?, ?, ?, ?, ?);", sqlConnection);

            for (AbstractLogEntry entry : entries) {

                preparedStatement.setString(1, entry.getType().toString());
                preparedStatement.setInt(2, entry.getRoomId());
                preparedStatement.setInt(3, entry.getPlayerId());
                preparedStatement.setString(4, entry.getString());
                preparedStatement.setInt(5, entry.getTimestamp());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
        }
    }

    public static RoomVisitLogEntry putRoomVisit(int playerId, int roomId, int entryTime) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("INSERT into player_room_visits (`player_id`, `room_id`, `time_enter`) VALUES(?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setInt(3, entryTime);

            LogDatabaseHelper.executeStatementSilently(preparedStatement, false);

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return new RoomVisitLogEntry(resultSet.getInt(1), playerId, roomId, entryTime);
            }
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
            LogDatabaseHelper.closeSilently(resultSet);
        }

        return null;
    }

    public static void updateRoomEntry(RoomVisitLogEntry entry) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("UPDATE player_room_visits SET time_exit = ? WHERE id = ?", sqlConnection);

            preparedStatement.setInt(1, entry.getExitTime());
            preparedStatement.setInt(2, entry.getId());

            LogDatabaseHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
        }
    }

    public static void updateRoomEntries() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("UPDATE player_room_visits SET time_exit = ? WHERE time_exit = 0", sqlConnection);

            preparedStatement.setInt(1, (int) Comet.getTime());

            LogDatabaseHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
        }
    }

    public static List<RoomChatLogEntry> getChatlogsByCriteria(int playerId, int roomId, int entryTime, int exitTime) {
        final int limit = 150;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<RoomChatLogEntry> chatlogs = new ArrayList<>();

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("SELECT `data`,`timestamp` FROM `logs` WHERE `timestamp` > ? AND `timestamp` < ? AND `type` = 'ROOM_CHATLOG' AND `user_id` = ? AND `room_id` = ? ORDER BY `timestamp` DESC LIMIT " + limit, sqlConnection);

            preparedStatement.setInt(1, entryTime);
            preparedStatement.setInt(2, exitTime == 0 ? (int) Comet.getTime() : exitTime);
            preparedStatement.setInt(3, playerId);
            preparedStatement.setInt(4, roomId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                chatlogs.add(new RoomChatLogEntry(roomId, playerId, resultSet.getString("data"), resultSet.getInt("timestamp")));
            }
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
            LogDatabaseHelper.closeSilently(resultSet);
        }

        return chatlogs;
    }

    public static List<RoomChatLogEntry> getChatlogsForRoom(int roomId, int startTimestamp, int endTimestamp) {
        final int limit = 50;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<RoomChatLogEntry> chatlogs = new ArrayList<>();

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("SELECT `user_id`,`data`,`timestamp` FROM `logs` WHERE `type` = 'ROOM_CHATLOG' AND `room_id` = ? AND `timestamp` > ? AND `timestamp` < ? ORDER BY `timestamp` DESC LIMIT " + limit, sqlConnection);

            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, startTimestamp);
            preparedStatement.setInt(3, endTimestamp);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                chatlogs.add(new RoomChatLogEntry(roomId, resultSet.getInt("user_id"), resultSet.getString("data"), resultSet.getInt("timestamp")));
            }
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
            LogDatabaseHelper.closeSilently(resultSet);
        }

        return chatlogs;
    }

    public static List<RoomChatLogEntry> getChatlogsForRoom(int roomId) {
        final int limit = 150;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<RoomChatLogEntry> chatlogs = new ArrayList<>();

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("SELECT `user_id`,`data`,`timestamp` FROM `logs` WHERE `type` = 'ROOM_CHATLOG' AND `room_id` = ? ORDER BY `timestamp` DESC LIMIT " + limit, sqlConnection);

            preparedStatement.setInt(1, roomId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                chatlogs.add(new RoomChatLogEntry(roomId, resultSet.getInt("user_id"), resultSet.getString("data"), resultSet.getInt("timestamp")));
            }
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
            LogDatabaseHelper.closeSilently(resultSet);
        }

        return chatlogs;
    }

    public static List<RoomVisitLogEntry> getLastRoomVisits(int playerId, int count) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<RoomVisitLogEntry> roomVisits = new ArrayList<>();

        try {
            sqlConnection = LogDatabaseHelper.getConnection();

            preparedStatement = LogDatabaseHelper.prepare("SELECT * FROM player_room_visits WHERE player_id = ? ORDER BY time_enter DESC LIMIT ?", sqlConnection);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, count);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomVisits.add(new RoomVisitLogEntry(resultSet.getInt("id"), playerId, resultSet.getInt("room_id"), resultSet.getInt("time_enter"), resultSet.getInt("time_exit")));
            }
        } catch (SQLException e) {
            LogDatabaseHelper.handleSqlException(e);
        } finally {
            LogDatabaseHelper.closeSilently(preparedStatement);
            LogDatabaseHelper.closeSilently(sqlConnection);
            LogDatabaseHelper.closeSilently(resultSet);
        }

        return roomVisits;
    }
}
