package com.habboproject.server.storage.queries.navigator;

import com.habboproject.server.game.navigator.types.Category;
import com.habboproject.server.game.navigator.types.categories.NavigatorViewMode;
import com.habboproject.server.game.navigator.types.publics.PublicRoom;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.utilities.collections.ConcurrentHashSet;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class NavigatorDao {
    public static Map<Integer, PublicRoom> getPublicRooms() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, PublicRoom> data = new ConcurrentHashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM navigator_publics WHERE enabled = '1' ORDER BY order_num ASC", sqlConnection);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final int roomId = resultSet.getInt("room_id");
                data.put(roomId, new PublicRoom(roomId, resultSet.getString("caption"), resultSet.getString("description"), resultSet.getString("image_url")));
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

    public static Set<Integer> getStaffPicks() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Set<Integer> data = new ConcurrentHashSet<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM navigator_staff_picks", sqlConnection);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final int roomId = resultSet.getInt("room_id");

                data.add(roomId);
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

    public static Map<Integer, Category> getCategories() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, Category> data = new ListOrderedMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM navigator_categories WHERE enabled = '1' ORDER BY order_id ASC", sqlConnection);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getInt("id"), new Category(
                        resultSet.getInt("id"),
                        resultSet.getString("category"),
                        resultSet.getString("category_identifier"),
                        resultSet.getString("public_name"),
                        true,
                        -1,
                        resultSet.getInt("required_rank"),
                        NavigatorViewMode.valueOf(resultSet.getString("view_mode").toUpperCase()),
                        resultSet.getString("category_type"),
                        resultSet.getString("search_allowance"),
                        resultSet.getInt("order_id"),
                        resultSet.getString("visible").equals("1"),
                        resultSet.getInt("room_count"),
                        resultSet.getInt("room_count_expanded")));
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

    public static void deleteStaffPick(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM navigator_staff_picks WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void staffPick(int roomId, int picker) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("INSERT into navigator_staff_picks VALUES(?, ?)", sqlConnection);

            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, picker);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
