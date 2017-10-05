package com.habboproject.server.storage.queries.effects;

import com.habboproject.server.game.effects.types.EffectItem;
import com.habboproject.server.game.players.components.types.inventory.InventoryEffect;
import com.habboproject.server.storage.SqlHelper;
import com.google.common.collect.Maps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by brend on 01/02/2017.
 */
public class EffectDao {
    public static Map<Integer, InventoryEffect> getEffectsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, InventoryEffect> data = Maps.newConcurrentMap();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM player_effects WHERE player_id = ?", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getInt("id"), create(resultSet));
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

    private static InventoryEffect create(ResultSet resultSet) throws SQLException {
        return new InventoryEffect(resultSet.getInt("id"), resultSet.getInt("effect_id"), (double) resultSet.getInt("effect_duration"), resultSet.getBoolean("is_totem"), resultSet.getBoolean("is_activated"), (double) resultSet.getInt("activated_stamp"), resultSet.getInt("quantity"));
    }

    public static int addEffect(int playerId, int effectId, int duration, boolean isTotem) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO player_effects (`player_id`, `effect_id`, `effect_duration`, `is_totem`, `is_activated`, `activated_stamp`, `quantity`) VALUES (?, ?, ?, ?, '0', 0, 1)", sqlConnection, (boolean) true);
            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, effectId);
            preparedStatement.setInt(3, duration);
            preparedStatement.setString(4, isTotem ? "1" : "0");
            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                int n = resultSet.getInt(1);
                return n;
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

    public static void loadEffectsPermissions(Map<Integer, EffectItem> effects) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM permission_effects", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int effectId = resultSet.getInt("effect_id");
                effects.put(effectId, new EffectItem(effectId, resultSet.getInt("min_rank"), resultSet.getBoolean("furni_effect"), resultSet.getBoolean("buyable_effect")));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
