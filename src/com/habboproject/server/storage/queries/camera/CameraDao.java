package com.habboproject.server.storage.queries.camera;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by brend on 03/03/2017.
 */
public class CameraDao {
    public static String buyPhoto(int photoId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT file_name FROM player_photos WHERE id = ? LIMIT 1",
                    sqlConnection);

            preparedStatement.setInt(1, photoId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString("file_name");
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

    public static void updateState(int photoId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_photos SET file_state = 'PURCHASED' WHERE id = ? LIMIT 1",
                    sqlConnection);

            preparedStatement.setInt(1, photoId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static int savePhoto(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("INSERT INTO player_photos (player_id, file_name, file_state, created_at) " +
                            "VALUES (?, '', 'PREVIEW', ?)",
                    sqlConnection, true);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setString(2, "" + (int) Comet.getTime());

            preparedStatement.execute();

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

    public static int saveThumbnail(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("INSERT INTO room_thumbnail (room_id, file_name, created_at) " +
                            "VALUES (?, '', ?)",
                    sqlConnection, true);

            preparedStatement.setInt(1, roomId);
            preparedStatement.setString(2, "" + (int) Comet.getTime());

            preparedStatement.execute();

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

    public static String getThumbnail(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("SELECT file_name FROM room_thumbnail WHERE room_id = ? ORDER BY id DESC LIMIT 1",
                    sqlConnection);

            preparedStatement.setInt(1, roomId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString("file_name");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return "";
    }
}
