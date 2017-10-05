package com.habboproject.server.storage.queries.items;

import com.habboproject.server.game.catalog.purchase.PurchaseEvent;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ItemDao {
    public static Map<Integer, ItemDefinition> getDefinitions() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, ItemDefinition> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM furniture", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
//                if(resultSet.getString("id").length() > 9) continue;
                try {
                    data.put(resultSet.getInt("id"), new ItemDefinition(resultSet));
                } catch (Exception e) {
                    ItemManager.getInstance().getLogger().warn("Error while loading item definition for ID: " + resultSet.getInt("id"), e);
                }
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

    public static long createItem(int userId, int itemId, String data) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into items (`user_id`, `room_id`, `base_item`, `extra_data`, `x`, `y`, `z`, `rot`, `wall_pos`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, 0);
            preparedStatement.setInt(3, itemId);
            preparedStatement.setString(4, data);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, 0);
            preparedStatement.setInt(8, 0);
            preparedStatement.setString(9, "");

            SqlHelper.executeStatementSilently(preparedStatement, false);

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return resultSet.getLong(1);
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

    public static List<Long> createItems(List<PurchaseEvent.CatalogPurchase> catalogPurchases) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Long> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into items (`user_id`, `room_id`, `base_item`, `group_id`, `extra_data`, `x`, `y`, `z`, `rot`, `wall_pos`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", sqlConnection, true);

            for (PurchaseEvent.CatalogPurchase purchase : catalogPurchases) {
                preparedStatement.setInt(1, purchase.getPlayerId());
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, purchase.getItemBaseId());
                preparedStatement.setInt(4, purchase.getGroupId());
                preparedStatement.setString(5, purchase.getData());
                preparedStatement.setInt(6, 0);
                preparedStatement.setInt(7, 0);
                preparedStatement.setInt(8, 0);
                preparedStatement.setInt(9, 0);
                preparedStatement.setString(10, "");

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                data.add(resultSet.getLong(1));
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

}
