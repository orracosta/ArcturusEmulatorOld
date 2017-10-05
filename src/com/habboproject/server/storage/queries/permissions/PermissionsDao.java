package com.habboproject.server.storage.queries.permissions;

import com.habboproject.server.game.permissions.types.CommandPermission;
import com.habboproject.server.game.permissions.types.Perk;
import com.habboproject.server.game.permissions.types.Rank;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class PermissionsDao {

    public static Map<Integer, Perk> getPerks() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, Perk> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `id`, `title`, `data`, `override_rank`, `override_default`, `min_rank` FROM permission_perks", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getInt("id"), new Perk(resultSet));
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

    public static Map<Integer, Rank> getRankPermissions() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, Rank> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("SELECT * FROM server_permissions_ranks", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.putIfAbsent(resultSet.getInt("id"), new Rank(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("flood_bypass").equals("1"),
                        resultSet.getInt("flood_time"),
                        resultSet.getString("disconnectable").equals("1"),
                        resultSet.getString("mod_tool").equals("1"),
                        resultSet.getString("bannable").equals("1"),
                        resultSet.getString("room_kickable").equals("1"),
                        resultSet.getString("room_full_control").equals("1"),
                        resultSet.getString("room_mute_bypass").equals("1"),
                        resultSet.getString("room_filter_bypass").equals("1"),
                        resultSet.getString("room_ignorable").equals("1"),
                        resultSet.getString("room_enter_full").equals("1"),
                        resultSet.getString("room_enter_locked").equals("1"),
                        resultSet.getString("room_staff_pick").equals("1"),
                        resultSet.getString("room_see_whispers").equals("1"),
                        resultSet.getString("messenger_staff_chat").equals("1"),
                        resultSet.getInt("messenger_max_friends"),
                        resultSet.getString("about_detailed").equals("1"),
                        resultSet.getString("about_stats").equals("1"),
                        resultSet.getString("ambassador_tool").equals("1"),
                        resultSet.getString("helper_tool").equals("1")));
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

    public static Map<String, CommandPermission> getCommandPermissions() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, CommandPermission> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("SELECT `command_id`, `minimum_rank`, `vip_only` FROM permission_commands", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                try {
                    data.putIfAbsent(resultSet.getString("command_id"), new CommandPermission(resultSet));
                } catch (Exception ignored) {

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
}
